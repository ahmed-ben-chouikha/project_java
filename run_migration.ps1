# Script PowerShell pour exécuter la migration SQL
# Cet script ajoute la colonne country à la table team

# Configuration - MODIFIE CES VALEURS SI BESOIN
$mysqlUser = "root"
$mysqlPassword = ""  # Laisse vide si pas de mot de passe, sinon mets le mot de passe
$mysqlHost = "localhost"
$databaseName = "connexion3a36"  # Change avec ton nom de base de données si différent
$sqlFile = "add_country_column.sql"

# Construis la commande MySQL
if ($mysqlPassword) {
    $mysqlCommand = "mysql -u $mysqlUser -p`"$mysqlPassword`" -h $mysqlHost $databaseName < $sqlFile"
} else {
    $mysqlCommand = "mysql -u $mysqlUser -h $mysqlHost $databaseName < $sqlFile"
}

Write-Host "Exécution de la migration SQL..." -ForegroundColor Green
Write-Host "Fichier: $sqlFile" -ForegroundColor Cyan
Write-Host "Base de données: $databaseName" -ForegroundColor Cyan
Write-Host ""

# Exécute la commande
try {
    Get-Content $sqlFile | mysql -u $mysqlUser -h $mysqlHost $databaseName
    Write-Host "✓ Migration complétée avec succès!" -ForegroundColor Green
    Write-Host "" -ForegroundColor Green
    Write-Host "La colonne 'country' a été ajoutée à la table 'team'." -ForegroundColor Green
} catch {
    Write-Host "✗ Erreur lors de la migration: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "Redémarre ton application JavaFX pour voir les changements." -ForegroundColor Yellow

