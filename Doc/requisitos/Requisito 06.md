# Requisito 6

## USER STORY
> **COMO** representante **QUERO** cadastrar tarefas de exclusão de lotes **PARA** serem executadas quando o produto estiver com data de validade menor que 3 semanas.

## CENÁRIO

> **CENÁRIO 1**: Um job é registrado para uma warehouse
> **DADO QUE** o usuário pertença a uma warehouse
> **E** exista eventos disponíveis cadastrados
> **E** que o representante pertence ao armazém  
> **QUANDO** o representante cadastrar um novo evento 
> **ENTÃO**, a ação cadastrada deve ser executada todos os dias
> **E** a data de última execução deve ser registrada  
> **E** uma url deve existir para execuções fora do horário
> 
## VALIDAÇÃO

- Autentique-se como representante e acesse os terminais
- Validação de parâmetros obrigatórios.

## SETUP INICIAL
Na raíz do projeto, primeiro crie e inicie os containers com:
```shell  
# Linux/macOS
docker-compose -f docker/docker-compose.yaml up -d
```

## Endpoints principais

### Observações
 **Para execução correta das rotas, é necessário que um job e seu método executor estejam devidamente cadastrados na table de job no banco de dados.**



### Vincular o job na warehouse
```shell
curl -X POST http://localhost:8080/api/v1/warehouse/jobs -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json" -d '{
    "idEvent": 1,
    "idWarehouse": 1,
    "productList": [
        1
    ]
}'
```

### Executar todos os jobs vinculados
```shell
curl -X POST http://localhost:8080/api/v1/warehouse/jobs/execute -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json"
```

## Endpoints auxiliares

### Lista todas as warehouses que possuem jobs vinculados
```shell
curl -X GET http://localhost:8080/api/v1/warehouse/jobs -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json"
```

### Lista todos os eventos cadastrados em uma única warehouse, com mais detalhes.
```shell
curl -X GET http://localhost:8080/api/v1/warehouse/{warehouseId}/jobs -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json"
```

### Detalha um evento vinculado á um armazém, com as informações de produtos vinculados
```shell
curl -X GET http://localhost:8080/api/v1/warehouse/jobs/detail/{warehouseJobId} -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json"
```

### Insere produtos na lista de consideração de um job vinculado
```shell
curl -X PATCH http://localhost:8080/api/v1/warehouse/jobs/products -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json" -d '{
    "warehouseEventId": 1,
    "productList": [
        1
    ]
}'
```

### Remove produtos na lista de consideração de um job vinculado
```shell
curl -X DELETE http://localhost:8080/api/v1/warehouse/jobs/products -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json" -d '{
    "warehouseEventId": 1,
    "productList": [
        1
    ]
}'
```

### Remove um job vinculado
```shell
curl -X DELETE http://localhost:8080/api/v1/warehouse/jobs/{warehouseJobId} -H "Authorization: Bearer {TOKEN}" -H "Content-Type: application/json" -d '{
    "warehouseEventId": 1,
    "productList": [
        1
    ]
}'
```