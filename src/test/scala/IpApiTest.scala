package com.ipquery.ipapi

class IpApiTest

class IpApiTest extends AnyFunSuite:
  test("IpApi.QueryOwnIP") {
    val result = IpApi.queryOwnIP()
    assert(result.isRight)
    assert(result.exists(_.trim.nonEmpty))
  }
  test("IpApi.QueryIP") {
    val result = IpApi.queryIP("8.8.8.8")
    assert(result.isRight)
    result match
      case Right(a) =>
        assert(a.isp.isDefined)
        assert(a.location.isDefined)
        assert(a.risk.isDefined)
      case Left(_) => assert(0 === 1)
  }