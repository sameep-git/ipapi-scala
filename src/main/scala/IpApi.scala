package com.ipquery.ipapi

import sttp.client4.quick.*
import sttp.client4.Response
import io.circe.generic.auto._
import io.circe.parser._

val BaseURL = "https://api.ipquery.io"

case class ISPInfo(
  asn: Option[String],
  org: Option[String],
  isp: Option[String]
)

case class LocationInfo(
  country:      Option[String],
  country_code:  Option[String],
  city:         Option[String],
  state:        Option[String],
  zipcode:      Option[String],
  latitude:     Option[Float],
  longitude:    Option[Float],
  timezone:     Option[String],
  localtime:    Option[String]
)

case class RiskInfo(
  is_mobile:     Option[Boolean],
  is_vpn:        Option[Boolean],
  is_tor:        Option[Boolean],
  is_proxy:      Option[Boolean],
  is_datacenter: Option[Boolean],
  risk_score:    Option[Int]
)

case class IPInfo(
  ip:       String,
  isp:      Option[ISPInfo],
  location: Option[LocationInfo],
  risk:     Option[RiskInfo]
)

object CirceIntegration:
  def parseJson(json: String): Either[io.circe.Error, IPInfo] =
    decode[IPInfo](json)

object main:
  def QueryOwnIP(): Either[String, String] =
    val response: Response[String] = quickRequest
      .get(uri"$BaseURL/")
      .send()

    if (response.is200) {
      Right(response.body)
    } else {
      Left(s"Error: ${response.statusText}")
    }

  def QueryIP(ip: String): Either[String, IPInfo] =
    val response: Response[String] = quickRequest
      .get(uri"$BaseURL/$ip")
      .send()

    CirceIntegration.parseJson(response.body) match
      case Right(data) => Right(data)
      case Left(error) => Left(s"Failed to parse JSON: $error")

  // TODO => Make it return: Either[String, List[IPInfo]]
  def QueryBulkIP(ips: List[String]): Unit =
    val ip_list = ips.mkString(",")
    val response: Response[String] = quickRequest
      .get(uri"$BaseURL/$ip_list")
      .send()
    // TODO