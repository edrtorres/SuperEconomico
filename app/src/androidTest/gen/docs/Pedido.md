

# Pedido


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **Integer** |  |  [optional] |
|**perfilId** | **UUID** |  |  [optional] |
|**estado** | [**EstadoEnum**](#EstadoEnum) |  |  [optional] |
|**total** | **BigDecimal** |  |  [optional] |
|**direccionId** | **Integer** |  |  [optional] |
|**metodoPago** | **String** |  |  [optional] |
|**creadoAt** | **OffsetDateTime** |  |  [optional] |



## Enum: EstadoEnum

| Name | Value |
|---- | -----|
| PENDIENTE | &quot;pendiente&quot; |
| COCINA | &quot;cocina&quot; |
| ENTREGA | &quot;entrega&quot; |
| CERRADO | &quot;cerrado&quot; |



