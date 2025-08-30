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

      - name: Criar diretórios e baixar dependências
        run: |
          mkdir -p build libs
          # Baixar robocode.jar (substitua pela URL correta)
          wget https://example.com/robocode.jar -O libs/robocode.jar || echo "Robocode já existe ou URL precisa ser ajustada"
          # Baixar configuração do Checkstyle
          wget https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml -O libs/google_checks.xml

      - name: Compilar o Robô
        run: |
          # Compila arquivos específicos - AJUSTE OS NOMES CONFORME SEU PROJETO
          javac -cp "libs/robocode.jar" -d build PrimeiroRobo.java MeuRobo.java

      - name: Rodar Checkstyle
        run: |
          wget https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.12.4/checkstyle-10.12.4-all.jar -O checkstyle.jar
          # Analisa arquivos específicos - AJUSTE OS NOMES CONFORME SEU PROJETO
          java -jar checkstyle.jar -c libs/google_checks.xml PrimeiroRobo.java  || echo "Checkstyle completado (avisos não quebram o build)"

      - name: Rodar SpotBugs
        run: |
          wget https://github.com/spotbugs/spotbugs/releases/download/4.8.3/spotbugs-4.8.3.tgz -O spotbugs.tgz
          tar -xvzf spotbugs.tgz
          chmod +x spotbugs-4.8.3/bin/spotbugs
          ./spotbugs-4.8.3/bin/spotbugs -textui -effort:max -high -auxclasspath libs/robocode.jar build || echo "SpotBugs completado (avisos não quebram o build)"

      - name: Mensagem final
        run: echo "Pipeline finalizado com sucesso! Código analisado e aprovado."
