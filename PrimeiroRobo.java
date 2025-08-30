name: Qualidade e Segurança

on:
push:
branches: [ "main" ]
pull_request:
branches: [ "main" ]

jobs:
qualidade-seguranca:
runs-on: ubuntu-latest

steps:
  - name: Checkout do código
    uses: actions/checkout@v4
  
  - name: Instalar Java 11
    uses: actions/setup-java@v4
    with:
      distribution: temurin
      java-version: 11
  
  - name: Compilar e Analisar o Robô
    run: |
      mkdir -p build libs
      
      # Baixar dependências necessárias
      wget https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.12.4/checkstyle-10.12.4-all.jar -O libs/checkstyle.jar
      wget https://github.com/spotbugs/spotbugs/releases/download/4.8.3/spotbugs-4.8.3.tgz -O spotbugs.tgz
      tar -xvzf spotbugs.tgz

      # Compilar o robô
      javac -cp libs/robocode.jar -d build PrimeiroRobo.java
      
      # Rodar Checkstyle
      echo "Executando Checkstyle..."
      java -jar libs/checkstyle.jar -c libs/google_checks.xml PrimeiroRobo.java

      # Rodar SpotBugs
      echo "Executando SpotBugs..."
      ./spotbugs-4.8.3/bin/spotbugs -textui -effort:max -high -auxclasspath libs/robocode.jar build

  - name: Mensagem final
    run: echo "Pipeline finalizado com sucesso! Código analisado."
