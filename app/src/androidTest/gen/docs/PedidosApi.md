# PedidosApi

All URIs are relative to *https://mvrlcbcydpubhovvrmvf.supabase.co*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**restV1PedidoItemsDelete**](PedidosApi.md#restV1PedidoItemsDelete) | **DELETE** /rest/v1/pedido_items | Quitar producto del pedido |
| [**restV1PedidoItemsGet**](PedidosApi.md#restV1PedidoItemsGet) | **GET** /rest/v1/pedido_items | Listar productos de un pedido |
| [**restV1PedidoItemsPatch**](PedidosApi.md#restV1PedidoItemsPatch) | **PATCH** /rest/v1/pedido_items | Actualizar cantidad de un producto |
| [**restV1PedidoItemsPost**](PedidosApi.md#restV1PedidoItemsPost) | **POST** /rest/v1/pedido_items | Insertar productos al pedido |
| [**restV1PedidosDelete**](PedidosApi.md#restV1PedidosDelete) | **DELETE** /rest/v1/pedidos | Eliminar un pedido |
| [**restV1PedidosGet**](PedidosApi.md#restV1PedidosGet) | **GET** /rest/v1/pedidos | Historial de pedidos del usuario |
| [**restV1PedidosPost**](PedidosApi.md#restV1PedidosPost) | **POST** /rest/v1/pedidos | Crear encabezado de pedido |


<a id="restV1PedidoItemsDelete"></a>
# **restV1PedidoItemsDelete**
> restV1PedidoItemsDelete(id)

Quitar producto del pedido

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PedidosApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://mvrlcbcydpubhovvrmvf.supabase.co");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    PedidosApi apiInstance = new PedidosApi(defaultClient);
    String id = "eq.1"; // String | 
    try {
      apiInstance.restV1PedidoItemsDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidosApi#restV1PedidoItemsDelete");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | [optional] |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Eliminado |  -  |

<a id="restV1PedidoItemsGet"></a>
# **restV1PedidoItemsGet**
> restV1PedidoItemsGet(pedidoId)

Listar productos de un pedido

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PedidosApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://mvrlcbcydpubhovvrmvf.supabase.co");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    PedidosApi apiInstance = new PedidosApi(defaultClient);
    String pedidoId = "eq.1"; // String | 
    try {
      apiInstance.restV1PedidoItemsGet(pedidoId);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidosApi#restV1PedidoItemsGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pedidoId** | **String**|  | [optional] |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Detalles del pedido |  -  |

<a id="restV1PedidoItemsPatch"></a>
# **restV1PedidoItemsPatch**
> restV1PedidoItemsPatch(id, restV1PedidoItemsPatchRequest)

Actualizar cantidad de un producto

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PedidosApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://mvrlcbcydpubhovvrmvf.supabase.co");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    PedidosApi apiInstance = new PedidosApi(defaultClient);
    String id = "eq.1"; // String | 
    RestV1PedidoItemsPatchRequest restV1PedidoItemsPatchRequest = new RestV1PedidoItemsPatchRequest(); // RestV1PedidoItemsPatchRequest | 
    try {
      apiInstance.restV1PedidoItemsPatch(id, restV1PedidoItemsPatchRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidosApi#restV1PedidoItemsPatch");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | [optional] |
| **restV1PedidoItemsPatchRequest** | [**RestV1PedidoItemsPatchRequest**](RestV1PedidoItemsPatchRequest.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Actualizado |  -  |

<a id="restV1PedidoItemsPost"></a>
# **restV1PedidoItemsPost**
> restV1PedidoItemsPost(pedidoItem)

Insertar productos al pedido

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PedidosApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://mvrlcbcydpubhovvrmvf.supabase.co");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    PedidosApi apiInstance = new PedidosApi(defaultClient);
    List<PedidoItem> pedidoItem = Arrays.asList(); // List<PedidoItem> | 
    try {
      apiInstance.restV1PedidoItemsPost(pedidoItem);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidosApi#restV1PedidoItemsPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **pedidoItem** | [**List&lt;PedidoItem&gt;**](PedidoItem.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Insertados |  -  |

<a id="restV1PedidosDelete"></a>
# **restV1PedidosDelete**
> restV1PedidosDelete(id)

Eliminar un pedido

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PedidosApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://mvrlcbcydpubhovvrmvf.supabase.co");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    PedidosApi apiInstance = new PedidosApi(defaultClient);
    String id = "eq.1"; // String | 
    try {
      apiInstance.restV1PedidosDelete(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidosApi#restV1PedidosDelete");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **String**|  | [optional] |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Eliminado |  -  |

<a id="restV1PedidosGet"></a>
# **restV1PedidosGet**
> restV1PedidosGet(perfilId, order)

Historial de pedidos del usuario

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PedidosApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://mvrlcbcydpubhovvrmvf.supabase.co");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    PedidosApi apiInstance = new PedidosApi(defaultClient);
    String perfilId = "eq.uuid"; // String | 
    String order = "creado_at.desc"; // String | 
    try {
      apiInstance.restV1PedidosGet(perfilId, order);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidosApi#restV1PedidosGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **perfilId** | **String**|  | [optional] |
| **order** | **String**|  | [optional] [default to creado_at.desc] |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Lista de pedidos |  -  |

<a id="restV1PedidosPost"></a>
# **restV1PedidosPost**
> restV1PedidosPost(prefer, pedido)

Crear encabezado de pedido

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PedidosApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://mvrlcbcydpubhovvrmvf.supabase.co");
    
    // Configure API key authorization: ApiKeyAuth
    ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
    ApiKeyAuth.setApiKey("YOUR API KEY");
    // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
    //ApiKeyAuth.setApiKeyPrefix("Token");

    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    PedidosApi apiInstance = new PedidosApi(defaultClient);
    String prefer = "return=representation"; // String | 
    Pedido pedido = new Pedido(); // Pedido | 
    try {
      apiInstance.restV1PedidosPost(prefer, pedido);
    } catch (ApiException e) {
      System.err.println("Exception when calling PedidosApi#restV1PedidosPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **prefer** | **String**|  | [optional] [default to return&#x3D;representation] |
| **pedido** | [**Pedido**](Pedido.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Creado |  -  |

