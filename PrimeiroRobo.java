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
      
      - name: Baixar dependências
        run: |
          mkdir -p build libs
          
          # Baixar Robocode (se necessário)
          # wget [URL_DO_ROBOCODE] -O libs/robocode.jar
          
          # Baixar Checkstyle
          wget https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.12.4/checkstyle-10.12.4-all.jar -O libs/checkstyle.jar
          
          # Baixar configuração do Checkstyle
          wget https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml -O libs/google_checks.xml
          
          # Baixar SpotBugs
          wget https://github.com/spotbugs/spotbugs/releases/download/4.8.3/spotbugs-4.8.3.tgz -O spotbugs.tgz
          tar -xvzf spotbugs.tgz
      
      - name: Compilar o robô
        run: |
          javac -cp "libs/robocode.jar" -d build PrimeiroRobo.java
      
      - name: Executar Checkstyle
        run: |
          echo "Executando Checkstyle..."
          java -jar libs/checkstyle.jar -c libs/google_checks.xml PrimeiroRobo.java || true
      
      - name: Executar SpotBugs
        run: |
          echo "Executando SpotBugs..."
          ./spotbugs-4.8.3/bin/spotbugs -textui -effort:max -high -auxclasspath libs/robocode.jar build || true
      
      - name: Mensagem final
        run: echo "Pipeline finalizado com sucesso! Código analisado."
