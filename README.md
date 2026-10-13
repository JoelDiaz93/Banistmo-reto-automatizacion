# Banistmo R010 - Reto técnico de Automatización

Automatización E2E construida para el reto R010 de Banistmo usando **Java 17 + Gradle + Serenity BDD + Screenplay + Selenium + Cucumber/Gherkin**.

## Flujo cubierto

```text
Personas
  -> Pymes
  -> Productos y servicios
  -> Asistencia pyme
  -> Solicítalo con tu Ejecutivo Pyme
  -> Validación del buscador emergente
  -> Tipo: Sucursales
  -> Provincia: Colón
  -> Validación: Zona Libre
```

La implementación no utiliza navegación directa como fallback para superar un paso fallido. Si un control obligatorio no está disponible o el portal cambia de manera incompatible, el escenario falla con evidencia.

## Requisitos
- Java 17
- Google Chrome
- Acceso a Internet
- Git

No es obligatorio instalar Gradle globalmente: el repositorio incluye un bootstrap que genera el **Gradle Wrapper oficial 9.2.1** en el primer uso si el JAR todavía no existe.

## Ejecución en Windows

```powershell
java -version
.\gradlew.bat --version
.\gradlew.bat clean test aggregate
```

## Ejecución Linux/macOS

```bash
java -version
./gradlew --version
./gradlew clean test aggregate
```

## Modo headless

```bash
./gradlew clean test aggregate -Dheadless.mode=true
```

## Reporte Serenity

Después de la ejecución:

```text
target/site/serenity/index.html
```

En Windows:

```powershell
start .\target\site\serenity\index.html
```

Serenity está configurado con `FOR_EACH_ACTION`, por lo que las acciones de negocio relevantes generan evidencia gráfica automáticamente.

## Arquitectura

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

Documentación complementaria:
- `docs/diseno-pruebas.md`
- `docs/trazabilidad.md`
- `docs/entrega.md`

## Parámetros

La URL de entrada puede sobrescribirse sin modificar código:

```bash
./gradlew test -Dchallenge.personas.url=https://www.banistmo.com/personas/
```

## CI

`.github/workflows/ci.yml` ejecuta el escenario en Java 17, modo headless, genera el reporte Serenity y lo conserva como artefacto de GitHub Actions incluso cuando la prueba falla.

## Calidad aplicada
- Screenplay real: Actor / Tasks / Interactions / Questions / Targets.
- Assertions intermedias para evitar falsos positivos.
- Sin `Thread.sleep`.
- Esperas explícitas.
- Locators semánticos.
- Manejo de modal, iframe o nueva ventana para el buscador.
- Logging reducido para facilitar diagnóstico.
- Trazabilidad requisito -> implementación -> evidencia.
- Configuración apta para ejecución local y CI.
