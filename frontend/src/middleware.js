import { NextResponse } from "next/server";
import { NextRequest } from "next/server";
import { cookies } from "next/headers";
import { ResponseCookie } from "next/dist/compiled/@edge-runtime/cookies";
import { ReadonlyRequestCookies } from "next/dist/server/web/spec-extension/adapters/request-cookies";

export async function middleware(req) {
  const cookieStore = req.cookies;
  const apiKey = cookieStore.get("apiKey")?.value;
  const accessToken = cookieStore.get("accessToken")?.value;
  const userType = cookieStore.get("userType")?.value;
  const authorization = "Bearer " + apiKey + " " + accessToken + " " + userType;

  // 토큰이 없는 경우 기본 처리
  if (!accessToken) {
    // 보호된 경로에 접근하려는 경우 리다이렉트
    if (isProtectedRouteAdmin(req.nextUrl.pathname)) {
      return createUnauthorizedResponse("/admin/login");
    }
    if (isProtectedRouteSeller(req.nextUrl.pathname) || isProtectedRouteCustomer(req.nextUrl.pathname)) {
      return createUnauthorizedResponse("/");
    }
    return NextResponse.next();
  }

  const { isLogin, isAccessTokenExpired, accessTokenPayload } = parseAccessToken(accessToken);

  // 토큰이 만료된 경우 갱신 시도
  if (isLogin && isAccessTokenExpired) {
    try {
      await refreshTokens(cookieStore);
    } catch (error) {
      console.error("토큰 갱신 실패:", error);
      // 토큰 갱신 실패 시 로그인 페이지로 리다이렉트
      return createUnauthorizedResponse("/");
    }
  }

  try {
    // 사용자 정보 확인
    const response = await fetch("http://localhost:8080/api/auth", {
      method: "GET",
      headers: {
        "Authorization": authorization
      }
    });

    if (!response.ok) {
      // API 응답이 실패한 경우 로그인 페이지로 리다이렉트
      return createUnauthorizedResponse("/");
    }

    const respData = await response.json();
    const isAdmin = respData?.userType === 'Admin';
    const isSeller = respData?.userType === 'Seller';
    const isCustomer = respData?.userType === 'Customer';

    // 이미 로그인한 사용자가 로그인/회원가입 페이지에 접근하는 경우 리다이렉트
    if (req.nextUrl.pathname === "/" || req.nextUrl.pathname === "/admin/login") {
      if (isSeller) return createUnauthorizedResponse("/sellers/info");
      else if (isCustomer) return createUnauthorizedResponse("/customers/info");
      else if (isAdmin) return createUnauthorizedResponse("/admin/management");
    }

    // 권한에 따른 접근 제어
    if (isProtectedRouteAdmin(req.nextUrl.pathname)) {
      if (!isAdmin) {
        return createUnauthorizedResponse("/admin/login");
      }
    } else if (isProtectedRouteSeller(req.nextUrl.pathname)) {
      if (!isAdmin && !isSeller) {
        return createUnauthorizedResponse("/");
      }
    } else if (isProtectedRouteCustomer(req.nextUrl.pathname)) {
      if (!isAdmin && !isCustomer) {
        return createUnauthorizedResponse("/");
      }
    }

    return NextResponse.next();
  } catch (error) {
    console.error("인증 처리 중 오류:", error);
    return createUnauthorizedResponse("/");
  }
}

function parseAccessToken(accessToken) {
  let isAccessTokenExpired = true;
  let accessTokenPayload = null;

  if (accessToken) {
    try {
      const tokenParts = accessToken.split(".");
      accessTokenPayload = JSON.parse(
        Buffer.from(tokenParts[1], "base64").toString()
      );
      const expTimestamp = accessTokenPayload.exp * 1000;
      isAccessTokenExpired = Date.now() > expTimestamp;
    } catch (e) {
      console.error("토큰 파싱 중 오류 발생:", e);
    }
  }

  const isLogin =
    typeof accessTokenPayload === "object" && accessTokenPayload !== null;

  return { isLogin, isAccessTokenExpired, accessTokenPayload };
}

async function refreshTokens(cookieStore) {
  const apiKey = cookieStore.get("apiKey")?.value;
  const accessToken = cookieStore.get("accessToken")?.value;
  const userType = cookieStore.get("userType")?.value;
  const authorization = "Bearer " + apiKey + " " + accessToken + " " + userType;

  try {
    const meResponse = await fetch("http://localhost:8080/api/auth", {
      method: "GET",
      headers: {
        "Authorization": authorization
      }
    });

    const setCookieHeader = meResponse.headers.get("Set-Cookie");
    
    if (setCookieHeader) {
      const cookies = setCookieHeader.split(",");

      for (const cookieStr of cookies) {
        const cookieData = parseCookie(cookieStr);

        if (cookieData) {
          const { name, value, options } = cookieData;
          if (name !== "accessToken" && name !== "apiKey" && name !== "userType") continue;

          cookieStore.set(name, value, options);
        }
      }
    }
  } catch(error) {
    console.log("토큰 갱신 중 오류:", error);
    return null;
  }
}

function parseCookie(cookieStr) {
  const parts = cookieStr.split(";").map((p) => p.trim());
  const [name, value] = parts[0].split("=");

  const options = {};
  for (const part of parts.slice(1)) {
    if (part.toLowerCase() === "httponly") options.httpOnly = true;
    else if (part.toLowerCase() === "secure") options.secure = true;
    else {
      const [key, val] = part.split("=");
      const keyLower = key.toLowerCase();
      if (keyLower === "domain") options.domain = val;
      else if (keyLower === "path") options.path = val;
      else if (keyLower === "max-age") options.maxAge = parseInt(val);
      else if (keyLower === "expires")
        options.expires = new Date(val).getTime();
      else if (keyLower === "samesite") {
        const sameSiteValue = val.toLowerCase();
        if (sameSiteValue === "lax" || sameSiteValue === "strict" || sameSiteValue === "none") {
          options.sameSite = sameSiteValue;
        }
      }
    }
  }

  return { name, value, options };
}

function isProtectedRouteAdmin(pathname) {
  return (
    pathname.startsWith("/admin/management")
  );
}
function isProtectedRouteSeller(pathname){
  return (
    pathname.startsWith("/estimate/create") ||
    pathname.startsWith("/sellers")
  );
}
function isProtectedRouteCustomer(pathname){
  return (
    pathname.startsWith("/customers") ||
    pathname.startsWith("/estimateRequest")
  );
}


 
function createUnauthorizedResponse(content){
  const loginUrl = new URL(content, process.env.NEXT_PUBLIC_BASE_URL || 'http://localhost:3000');

  return NextResponse.redirect(loginUrl);
}
export const config = {
  // 아래 2가지 경우에는 middleware를 실행하지 않도록 세팅
  // api 로 시작하거나 하는 요청 : /api/~~~
  // 정적 파일 요청 : /~~~.jpg, /~~~.png, /~~~.css, /~~~.js
  // PS. 여기서 말하는 api 로 시작하는 요청은 백엔드 API 서버로의 요청이 아니라 Next.js 고유의 API 서버로의 요청이다.
  // PS. 우리는 현재 이 기능을 사용하고 있지 않다.
  matcher: "/((?!.*\\.|api\\/).*)",
};
