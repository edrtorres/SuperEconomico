# CatlogoApi

All URIs are relative to *https://mvrlcbcydpubhovvrmvf.supabase.co*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**restV1CategoriasGet**](CatlogoApi.md#restV1CategoriasGet) | **GET** /rest/v1/categorias | Listar categorías |
| [**restV1ProductosGet**](CatlogoApi.md#restV1ProductosGet) | **GET** /rest/v1/productos | Listar productos (con filtros) |


<a id="restV1CategoriasGet"></a>
# **restV1CategoriasGet**
> restV1CategoriasGet(select)

Listar categorías

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CatlogoApi;

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

    CatlogoApi apiInstance = new CatlogoApi(defaultClient);
    String select = "*"; // String | 
    try {
      apiInstance.restV1CategoriasGet(select);
    } catch (ApiException e) {
      System.err.println("Exception when calling CatlogoApi#restV1CategoriasGet");
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
| **select** | **String**|  | [optional] [default to *] |

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
| **200** | Lista de categorías |  -  |

<a id="restV1ProductosGet"></a>
# **restV1ProductosGet**
> restV1ProductosGet(select, categoriaId, estaActivo)

Listar productos (con filtros)

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CatlogoApi;

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

    CatlogoApi apiInstance = new CatlogoApi(defaultClient);
    String select = "*"; // String | 
    String categoriaId = "eq.1"; // String | 
    String estaActivo = "eq.true"; // String | 
    try {
      apiInstance.restV1ProductosGet(select, categoriaId, estaActivo);
    } catch (ApiException e) {
      System.err.println("Exception when calling CatlogoApi#restV1ProductosGet");
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
| **select** | **String**|  | [optional] [default to *] |
| **categoriaId** | **String**|  | [optional] |
| **estaActivo** | **String**|  | [optional] [default to eq.true] |

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
| **200** | Lista de productos |  -  |

