# AutenticacinApi

All URIs are relative to *https://mvrlcbcydpubhovvrmvf.supabase.co*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**authV1SignupPost**](AutenticacinApi.md#authV1SignupPost) | **POST** /auth/v1/signup | Crear cuenta de cliente |
| [**authV1TokenPost**](AutenticacinApi.md#authV1TokenPost) | **POST** /auth/v1/token | Iniciar sesión (Login) |
| [**authV1UserPut**](AutenticacinApi.md#authV1UserPut) | **PUT** /auth/v1/user | Actualizar contraseña |


<a id="authV1SignupPost"></a>
# **authV1SignupPost**
> authV1SignupPost(registroRequest)

Crear cuenta de cliente

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AutenticacinApi;

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

    AutenticacinApi apiInstance = new AutenticacinApi(defaultClient);
    RegistroRequest registroRequest = new RegistroRequest(); // RegistroRequest | 
    try {
      apiInstance.authV1SignupPost(registroRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling AutenticacinApi#authV1SignupPost");
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
| **registroRequest** | [**RegistroRequest**](RegistroRequest.md)|  | [optional] |

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
| **200** | Éxito |  -  |

<a id="authV1TokenPost"></a>
# **authV1TokenPost**
> AuthResponse authV1TokenPost(grantType, authV1TokenPostRequest)

Iniciar sesión (Login)

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AutenticacinApi;

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

    AutenticacinApi apiInstance = new AutenticacinApi(defaultClient);
    String grantType = "password"; // String | 
    AuthV1TokenPostRequest authV1TokenPostRequest = new AuthV1TokenPostRequest(); // AuthV1TokenPostRequest | 
    try {
      AuthResponse result = apiInstance.authV1TokenPost(grantType, authV1TokenPostRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AutenticacinApi#authV1TokenPost");
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
| **grantType** | **String**|  | [optional] [default to password] |
| **authV1TokenPostRequest** | [**AuthV1TokenPostRequest**](AuthV1TokenPostRequest.md)|  | [optional] |

### Return type

[**AuthResponse**](AuthResponse.md)

### Authorization

[ApiKeyAuth](../README.md#ApiKeyAuth), [BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Éxito |  -  |

<a id="authV1UserPut"></a>
# **authV1UserPut**
> authV1UserPut(authV1UserPutRequest)

Actualizar contraseña

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AutenticacinApi;

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

    AutenticacinApi apiInstance = new AutenticacinApi(defaultClient);
    AuthV1UserPutRequest authV1UserPutRequest = new AuthV1UserPutRequest(); // AuthV1UserPutRequest | 
    try {
      apiInstance.authV1UserPut(authV1UserPutRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling AutenticacinApi#authV1UserPut");
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
| **authV1UserPutRequest** | [**AuthV1UserPutRequest**](AuthV1UserPutRequest.md)|  | [optional] |

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
| **200** | Éxito |  -  |

