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
  
  - name: Compilar o Robô
    run: |
      mkdir -p build
      if [ ! -f libs/robocode.jar ]; then
        echo "libs/robocode.jar não encontrado. Certifique-se de que ele está na pasta libs/." >&2
        exit 1
      fi
      javac -cp libs/robocode.jar -d build PrimeiroRobo.java

  - name: Rodar Checkstyle
    run: |
      wget https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.12.4/checkstyle-10.12.4-all.jar -O checkstyle.jar
      java -jar checkstyle.jar -c libs/google_checks.xml PrimeiroRobo.java

  - name: Rodar SpotBugs
    run: |
      wget https://github.com/spotbugs/spotbugs/releases/download/4.8.3/spotbugs-4.8.3.tgz -O spotbugs.tgz
      tar -xvzf spotbugs.tgz
      chmod +x spotbugs-4.8.3/bin/spotbugs
      if [ ! -d build ] || [ -z "$(ls -A build)" ]; then
        echo "Nenhuma classe compilada encontrada em build/. Pulando SpotBugs."
        exit 0
      fi
      ./spotbugs-4.8.3/bin/spotbugs -textui -effort:max -high -auxclasspath libs/robocode.jar build

  - name: Mensagem final
    run: echo "Pipeline finalizado com sucesso! Código analisado."
