// Cria usuário específico para a aplicação (não usar admin em produção)
db.createUser({
  user: "cliente_micro",
  pwd: "123_321",
  roles: [
    { role: "readWrite", db: "db_cliente" },
    { role: "dbAdmin", db: "db_cliente" }
  ]
});