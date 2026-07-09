# PerfilesApi

All URIs are relative to *https://mvrlcbcydpubhovvrmvf.supabase.co*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**restV1PerfilesGet**](PerfilesApi.md#restV1PerfilesGet) | **GET** /rest/v1/perfiles | Buscar perfil (por ID o Teléfono) |
| [**restV1PerfilesPatch**](PerfilesApi.md#restV1PerfilesPatch) | **PATCH** /rest/v1/perfiles | Actualizar información del perfil |


<a id="restV1PerfilesGet"></a>
# **restV1PerfilesGet**
> restV1PerfilesGet(id, telefono, select)

Buscar perfil (por ID o Teléfono)

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PerfilesApi;

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

    PerfilesApi apiInstance = new PerfilesApi(defaultClient);
    String id = "eq.uuid"; // String | 
    String telefono = "eq.95212400"; // String | 
    String select = "*"; // String | 
    try {
      apiInstance.restV1PerfilesGet(id, telefono, select);
    } catch (ApiException e) {
      System.err.println("Exception when calling PerfilesApi#restV1PerfilesGet");
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
| **telefono** | **String**|  | [optional] |
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
| **200** | Datos del perfil |  -  |

<a id="restV1PerfilesPatch"></a>
# **restV1PerfilesPatch**
> restV1PerfilesPatch(id, perfil)

Actualizar información del perfil

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.PerfilesApi;

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

    PerfilesApi apiInstance = new PerfilesApi(defaultClient);
    String id = "eq.uuid"; // String | 
    Perfil perfil = new Perfil(); // Perfil | 
    try {
      apiInstance.restV1PerfilesPatch(id, perfil);
    } catch (ApiException e) {
      System.err.println("Exception when calling PerfilesApi#restV1PerfilesPatch");
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
| **perfil** | [**Perfil**](Perfil.md)|  | [optional] |

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
| **204** | Actualizado correctamente |  -  |

