$file = "src/main/java/edu/connexion3a36/rankup/api/AuthApiController.java"
$content = Get-Content $file -Raw
$content = $content -replace 'catch \(SQLException e\) \{\s*response\.put\("success", false\);\s*response\.put\("message", "Error: " \+ e\.getMessage\(\)\);\s*response\.put\("code", 500\);\s*\}', 'catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("code", 500);
        }'
Set-Content $file $content