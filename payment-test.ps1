<#
payment-test.ps1
Creates a payment (debug create-as), optionally marks success, then shows DB row.

Usage:
  # Create + mark success:
  .\payment-test.ps1 -AuctionId 2 -BuyerEmail "buyer123@gmail.com" -PaymentMethod "UPI" -MarkSuccess

  # Create only:
  .\payment-test.ps1 -AuctionId 3 -BuyerEmail "buyer123@gmail.com"

Notes:
 - Uses Invoke-RestMethod (PowerShell-native) so no curl alias/quoting problems.
 - Expects DB container name "demo-db" and root password "root" (change params if needed).
#>

param(
    [int]$AuctionId = 2,
    [string]$PaymentMethod = "UPI",
    [string]$BuyerEmail = "buyer123@gmail.com",
    [switch]$MarkSuccess,
    [string]$ApiBase = "http://localhost:8080",
    [string]$DbContainer = "demo-db",
    [string]$DbRootPassword = "root",
    [int]$HttpTimeoutSec = 15
)

function Throw-If($cond, $message) {
    if ($cond) { throw $message }
}

Write-Host "Starting payment-test -> AuctionId=$AuctionId Buyer=$BuyerEmail Method=$PaymentMethod" -ForegroundColor Cyan

# Build request body
$bodyObject = @{ auctionId = $AuctionId; paymentMethod = $PaymentMethod }
$bodyJson = $bodyObject | ConvertTo-Json -Depth 5

# endpoint (debug create-as)
$createUrl = "$ApiBase/api/payments/debug/create-as/$($BuyerEmail)"

# Create payment
try {
    Write-Host "Calling: POST $createUrl" -ForegroundColor DarkGray
    $createResp = Invoke-RestMethod -Uri $createUrl -Method Post -Body $bodyJson -ContentType 'application/json' -TimeoutSec $HttpTimeoutSec -ErrorAction Stop
} catch {
    Write-Host "ERROR: create payment failed." -ForegroundColor Red
    $_ | Format-List -Force
    exit 1
}

if (-not $createResp) {
    Write-Host "ERROR: empty response from create endpoint." -ForegroundColor Red
    exit 1
}

Write-Host "`n-- Create response --" -ForegroundColor Green
$createResp | Format-List

# Payment id check
if (-not ($createResp.PSObject.Properties.Name -contains 'id') -or -not $createResp.id) {
    Write-Host "ERROR: create response does not contain 'id' -> cannot continue DB query/mark-success." -ForegroundColor Red
    exit 1
}

$paymentId = $createResp.id
Write-Host "`nPayment created: id = $paymentId" -ForegroundColor Green

# Optional: mark as success
if ($MarkSuccess) {
    Write-Host "`nMarking payment as SUCCESS..." -ForegroundColor Cyan
    $ts = [int](Get-Date -UFormat %s)
    $gatewayId = "GW_TEST_$ts"
    $successUrl = "$ApiBase/api/payments/$paymentId/success?gatewayPaymentId=$gatewayId"
    try {
        Write-Host "Calling: POST $successUrl" -ForegroundColor DarkGray
        $successResp = Invoke-RestMethod -Uri $successUrl -Method Post -Body '{}' -ContentType 'application/json' -TimeoutSec $HttpTimeoutSec -ErrorAction Stop
        Write-Host "`n-- Success response --" -ForegroundColor Green
        $successResp | Format-List
    } catch {
        Write-Host "ERROR: mark-success failed." -ForegroundColor Red
        $_ | Format-List -Force
        exit 1
    }
}

# Query DB row (only if we have a paymentId)
Write-Host "`nQuerying DB container '$DbContainer' for payment id $paymentId..." -ForegroundColor Cyan

# Compose SQL carefully — escape nothing because it's numeric id
$sql = "USE marketplace; SELECT id,auction_id,amount,status,gateway_payment_id,created_at,updated_at FROM payments WHERE id=$paymentId;"

# Build docker command array for robust execution
$dockerArgs = @('exec','-i',$DbContainer,'mysql','-uroot',"-p$DbRootPassword","-e",$sql)

try {
    # Use Start-Process to capture output reliably
    $proc = Start-Process -FilePath docker -ArgumentList $dockerArgs -NoNewWindow -RedirectStandardOutput -RedirectStandardError -PassThru -Wait
    $stdout = $proc.StandardOutput.ReadToEnd().Trim()
    $stderr = $proc.StandardError.ReadToEnd().Trim()
    if ($stderr) {
        Write-Host "`n-- DB STDERR --" -ForegroundColor Yellow
        $stderr
    }
    Write-Host "`n-- DB STDOUT --" -ForegroundColor Green
    $stdout
} catch {
    Write-Host "ERROR: docker/mysql query failed. Is Docker running and is container name correct?" -ForegroundColor Red
    $_ | Format-List -Force
    exit 1
}

Write-Host "`nFinished." -ForegroundColor Magenta
