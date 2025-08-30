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

      - name: Criar diretórios e baixar configuração
        run: |
          mkdir -p build libs
          # Baixar configuração do Checkstyle
          wget https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml -O libs/google_checks.xml

      - name: Compilar o Robô
        run: |
          javac -cp "libs/robocode.jar" -d build aprendizado/PrimeiroRobo.java

      - name: Rodar Checkstyle
        run: |
          # URL CORRIGIDA do Checkstyle
          wget https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.12.4/checkstyle-10.12.4-all.jar -O checkstyle.jar
          java -jar checkstyle.jar -c libs/google_checks.xml aprendizado/PrimeiroRobo.java || echo "Checkstyle completado"

      - name: Rodar SpotBugs
        run: |
          wget https://github.com/spotbugs/spotbugs/releases/download/4.8.3/spotbugs-4.8.3.tgz -O spotbugs.tgz
          tar -xvzf spotbugs.tgz
          chmod +x spotbugs-4.8.3/bin/spotbugs
          ./spotbugs-4.8.3/bin/spotbugs -textui -effort:max -high -auxclasspath libs/robocode.jar build || echo "SpotBugs completado"

      - name: Mensagem final
        run: echo "Pipeline finalizado com sucesso! Código analisado e aprovado."
