# bounce the docker volume xoom_schemata_postgres in the docker-compose.yml
docker-compose -p "dev" down
docker volume rm xoom_schemata_postgres
docker-compose -p "dev" up -d
