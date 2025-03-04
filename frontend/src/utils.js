function getAuthorizationHeader(){
  const headers = new Headers();
  const cookieStore = cookies();
  const apiKey = cookieStore.get("apiKey")?.value;
  const accessToken = cookieStore.get("accessToken")?.value;
  const userType = cookieStore.get("userType")?.value;
  const authorization = "Bearer " + apiKey +" " + accessToken +" " +userType;
  headers.set('Authorization', authorization);
  return headers;
  }