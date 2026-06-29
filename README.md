Tests

1. Lancer les deux services :
   - badwallet-api : port 8080
   - payment-service : port 8081

2. Ouvrir le fichier `test.http` avec IntelliJ IDEA ou VS Code REST Client

3. Exécuter les requêtes dans l'ordre :
   - Les requêtes 1 et 2 créent les wallets
   - La requête 3 liste les wallets : **copier un ID**
   - Coller cet ID dans les requêtes 6 à 13 (remplacer `{ID_COPIÉ}`)

4. Tous les tests doivent passer avec des réponses HTTP 200.