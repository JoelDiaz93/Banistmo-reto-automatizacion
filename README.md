# Banistmo R010 - Reto técnico de Automatización

[![Banistmo R010 Automation CI](https://github.com/JoelDiaz93/Banistmo-reto-automatizacion/actions/workflows/ci.yml/badge.svg)](https://github.com/JoelDiaz93/Banistmo-reto-automatizacion/actions/workflows/ci.yml)

Automatización E2E del reto **R010 de Banistmo**, desarrollada con **Java 17, Gradle, Serenity BDD, Screenplay, Selenium y Cucumber/Gherkin**.

El flujo valida la navegación hasta **Asistencia Pyme**, accede al buscador de sucursales, aplica **Tipo de punto = Sucursales** y **Provincia = Colón**, comprueba que existan exactamente **2 sucursales** y verifica que **Zona Libre** esté asociada a **Colón**.

## Estado de la solución

- Java 17.
- Serenity BDD 5.3.11.
- Gradle Wrapper.
- Screenplay Pattern.
- Cucumber / Gherkin.
- Selenium WebDriver.
- Pruebas unitarias para lógica propia.
- Reporte Serenity con evidencia `AFTER_EACH_STEP`.
- GitHub Actions con ejecución manual y automática.
- Quality gate P0 y ejecución reproducible.

## Flujo automatizado

```text
Personas
  → Pymes
  → Productos y servicios
  → Asistencia pyme
  → Solicítalo con tu Ejecutivo Pyme
  → Buscador de sucursales
  → Tipo de punto: Sucursales
  → Provincia: Colón
  → Validación: 2 sucursales
  → Validación: Zona Libre asociada a Colón
```

## Escenario BDD

```gherkin
# language: es
@banistmo @r010 @e2e @screenplay
Característica: Asistencia Pyme y búsqueda de sucursales
  Como usuario del portal Banistmo
  Quiero consultar el Plan de Asistencia Pyme y localizar una sucursal
  Para verificar que el flujo de atención presencial funciona correctamente

  Escenario: Consultar Asistencia Pyme y validar una sucursal en Colón
    Dado que el usuario ingresa al portal de Personas de Banistmo
    Cuando navega a la sección Pymes
    Y selecciona Productos y servicios
    Y ingresa a Asistencia pyme
    Y selecciona Solicítalo con tu Ejecutivo Pyme
    Entonces debe visualizar el buscador emergente de sucursales correcto
    Cuando busca el tipo de punto "Sucursales" en la provincia "Colón"
    Entonces debe visualizar 2 sucursales en la provincia "Colón"
    Y debe visualizar la sucursal "Zona Libre" asociada a "Colón"
```

## Arquitectura

```text
Feature / Gherkin
      ↓
Step Definitions
      ↓
Tasks / Questions
      ↓
Interactions
      ↓
Targets / User Interfaces
      ↓
Serenity + Selenium WebDriver
      ↓
Portal Banistmo
```

Estructura principal:

```text
src/test/java/com/banistmo/automation/
├── config/
├── interactions/
├── questions/
├── runners/
├── stepdefinitions/
├── tasks/
└── userinterfaces/

src/test/resources/
├── features/
├── logback-test.xml
└── serenity.conf
```

## Requisitos para ejecución local

- Java 17.
- Google Chrome.
- Git.
- Acceso a Internet.
- PowerShell 5.1+ en Windows para los scripts de quality gate.

No es obligatorio instalar Gradle globalmente. El proyecto dispone de bootstrap para generar el **Gradle Wrapper oficial** cuando el JAR no está disponible.

## Clonar el repositorio

```bash
git clone https://github.com/JoelDiaz93/Banistmo-reto-automatizacion.git
cd Banistmo-reto-automatizacion
```

## Ejecución en Windows

Verificar Java:

```powershell
java -version
```

Verificar el wrapper:

```powershell
.\gradlew.bat --version
```

Ejecución recomendada:

```powershell
.\scripts\run-r010.ps1
```

Ejecución directa equivalente:

```powershell
.\gradlew.bat clean test aggregate
```

Abrir el reporte:

```powershell
start .\target\site\serenity\index.html
```

## Ejecución en Linux/macOS

```bash
java -version
./gradlew --version
./gradlew clean test aggregate
```

Reporte:

```text
target/site/serenity/index.html
```

## Ejecución headless

```bash
./gradlew clean test aggregate -Dheadless.mode=true
```

En Windows también puede utilizarse:

```powershell
.\scripts\run-r010.ps1 -Headless
```

## Quality Gate P0

Antes de cerrar la entrega puede ejecutarse:

```powershell
.\scripts\verify-p0.ps1 -RunE2E
```

El gate valida aspectos estáticos del repositorio y ejecuta el escenario R010. La aceptación funcional de la entrega está determinada por el escenario solicitado en el reto y por el reporte generado por Serenity BDD.

## GitHub Actions

Workflow:

```text
.github/workflows/ci.yml
```

Nombre:

```text
Banistmo R010 Automation CI
```

Se ejecuta:

- con `push` sobre `main`;
- con `pull_request` hacia `main`;
- manualmente mediante `workflow_dispatch`.

### Ejecución manual desde GitHub

1. Abrir la pestaña **Actions**.
2. Seleccionar **Banistmo R010 Automation CI**.
3. Presionar **Run workflow**.
4. Seleccionar `main`.
5. Ejecutar el workflow.
6. Esperar la finalización del job `e2e`.

El pipeline:

- configura Temurin Java 17;
- utiliza caché de Gradle;
- valida el Gradle Wrapper;
- ejecuta `p0StaticCheck`;
- ejecuta el E2E en Chrome headless;
- genera el reporte Serenity;
- publica el reporte incluso si la prueba falla.

## Evidencia en GitHub

Ruta:

```text
Actions
  → Banistmo R010 Automation CI
  → ejecución
  → Artifacts
  → serenity-report
```

El artifact contiene:

```text
target/site/serenity/
```

La configuración actual conserva el reporte durante **14 días**.

## Reporte Serenity

Reporte principal:

```text
target/site/serenity/index.html
```

Resumen:

```text
target/site/serenity/serenity-summary.html
```

Serenity está configurado con:

- Chrome.
- `implicitlywait = 0`.
- `fluentwait = 3000`.
- `pageLoadStrategy = eager`.
- viewport `1440 x 1000`.
- evidencia `AFTER_EACH_STEP`.
- reinicio del navegador por escenario.

## Parámetros

La URL de entrada puede sobrescribirse sin modificar código:

```bash
./gradlew test -Dchallenge.personas.url=https://www.banistmo.com/personas/
```

Windows:

```powershell
.\gradlew.bat test -Dchallenge.personas.url=https://www.banistmo.com/personas/
```

## Calidad aplicada

- Screenplay con responsabilidades separadas.
- Tasks, Interactions, Questions y Targets.
- Esperas explícitas y sin `Thread.sleep`.
- Manejo centralizado de privacidad y overlays conocidos.
- Política adaptativa para la carga del filtro de provincia.
- Conteo exacto de resultados.
- Validación de nombre y provincia en el mismo resultado.
- Evidencia estabilizada mediante política de viewport.
- Pruebas unitarias para lógica determinista propia.
- JUnit administrado mediante BOM.
- Quality gate P0.
- CI reproducible con reporte Serenity descargable.

## Informe técnico

La explicación de las decisiones de diseño, cumplimiento del reto, evolución por commits, comandos Git y resultado de ejecución se encuentra en:

[`Informe_Tecnico_Banistmo_R010.pdf`](./Informe_Tecnico_Banistmo_R010.pdf)

## Evolución del proyecto

```text
01  Inicio del proyecto con Screenplay
02  Manejo del aviso de privacidad y ventanas emergentes
03  Ajuste del acceso al buscador de sucursales
04  Selección de filtros con scroll automático
05  Validación de las sucursales disponibles en Colón
06  Optimización de tiempos de espera y capturas
07  Ajuste de la selección de provincia
08  Mejora de evidencias en el reporte de Serenity
09  Ajustes finales de calidad y documentación
```

## Entrega final

La versión evaluable se identifica con el tag:

```text
v1.0.0-r010
```

Antes de crear el tag:

```powershell
git status
.\scripts\verify-p0.ps1 -RunE2E
```

También debe verificarse una ejecución satisfactoria de **GitHub Actions** sobre el commit final.

Una vez publicado `v1.0.0-r010`, cualquier mejora posterior debe realizarse como una versión distinta para conservar la integridad de la entrega.

## Autor

**Carlos Díaz**

- GitHub: https://github.com/JoelDiaz93
- LinkedIn: https://www.linkedin.com/in/cjoeldiaz/
- Portafolio: https://carlosdiazec.com
