// proxy.ts (Next.js 16 – giữ nguyên tên file và cấu trúc của bạn)
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

const protectedRoutes = [
  '/vendor',
  '/admin',
]

export async function proxy(request: NextRequest) {
  const pathname = request.nextUrl.pathname

  const isProtected = protectedRoutes.some(route => 
    pathname === route || pathname.startsWith(route + '/')
  )

  if (!isProtected) {
    return NextResponse.next()
  }

  const token = request.cookies.get('SESSION')?.value

  if (!token) {
    const loginUrl = new URL('/', request.url)
    loginUrl.searchParams.set('callbackUrl', pathname)
    return NextResponse.redirect(loginUrl)
  }

  try {
    const apiRes = await fetch(
      `http://api-gateway:8000/api/vendor/vendor-registration/me`,
      {
        method: 'GET',
        headers: {
          Cookie: `SESSION=${token}`,
        },
        credentials: 'include',
      }
    )

    const result = await apiRes.json()
    console.log('API Response Data:', result);

    if (apiRes.status === 401) {
      await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/logout`, {
        method: 'POST', // hoặc GET tùy backend
        headers: {
          Cookie: `SESSION=${token}`,
        },
        credentials: 'include',
      }).catch(err => {
        console.warn('Gọi logout backend thất bại (có thể không sao):', err)
      })
    }

    if (!apiRes.ok) {
      throw new Error('Failed to fetch vendor data')
    }

    const basicInfo = result?.data;

    const requiredFieldsBasic: (keyof NonNullable<typeof basicInfo>)[] = [
      'name',
      'slug',
      'logoUrl',
      'profile'
    ]

    const hasAllRequired = basicInfo && requiredFieldsBasic.every(field => {
        const value = basicInfo[field]
        return typeof value === 'string' ? value.trim() !== '' : !!value
      }) &&
      basicInfo.profile &&
      ['email'].every(field => {
        const value = basicInfo.profile[field]
        return typeof value === 'string' && value.trim() !== ''
    })


    if (!hasAllRequired) {
      const completeUrl = new URL('/vendor/register', request.url)
      completeUrl.searchParams.set('from', pathname)
      return NextResponse.redirect(completeUrl)
    }

    return NextResponse.next()

  } catch (error) {
    console.error('[proxy.ts] Vendor profile check failed:', error)
    return NextResponse.redirect(new URL('/vendor/register', request.url))
  }
}

export const config = {
  matcher: [
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
}