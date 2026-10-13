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
    Entonces debe visualizar la sucursal "Zona Libre" asociada a "Colón"
