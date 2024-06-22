# O que é esse projeto?

Esse projeto é o backend do sistema web que está sendo desenvolvido.

## Como executar

No diretório repositório rode o seguinte comando para buildar o programa:

``` ./gradlew build```

Após isso, rode o backend com o seguinte comando:

``` ./gradlew run```

A partir daí o backend estará rodando no ```localhost:8080```

## Como configurar o DB

Para configurar o db no ubuntu você pode seguir esse tutorial (apenas os steps 1 e 2): 
https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-neo4j-on-ubuntu-20-04

Após isso haverá um database default rodando no seu pc.

## Como configurar o .env

O .env possui as chaves para acessar a API do spotify. Insira o clientid e o client secret
dessa forma no .env

```
CLIENTID=clientid
CLIENTSECRET=clientsecret
```

Entre em contato com o filipe para pegar essas chaves.

## Como executar os testes

Para executar os testes, basta executar o seguinte comando:

```
./gradlew test
```