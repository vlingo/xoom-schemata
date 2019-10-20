# bounce the docker volume vlingo_schemata_postgres in the docker-compose.yml
docker-compose -p "dev" down
docker volume rm vlingo_schemata_postgres
docker-compose -p "dev" up -d
