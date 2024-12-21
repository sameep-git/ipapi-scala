package com.ipqwery.ipapi

import sttp.client4.quick.*
import sttp.client4.Response
import io.circe.generic.auto._
import io.circe.parser._

// defining the base URL for ipquery
val BaseURL = "https://api.ipquery.io"

/**
 * This case class represents information about an ISP (Internet Service Provider).
 *
 * @param asn The Autonomous System Number (ASN) of the ISP.
 * @param org The organization associated with the ISP.
 * @param isp The name of the ISP.
 */
case class ISPInfo(
  asn: Option[String],
  org: Option[String],
  isp: Option[String]
)

/**
 * This case class represents information about the geographical location of an IP Address.
 *
 * @param country The country name.
 * @param country_code The ISO country code.
 * @param city The city name.
 * @param state The state or region name.
 * @param zipcode The postal or ZIP code.
 * @param latitude The latitude of the location.
 * @param longitude The longitude of the location.
 * @param timezone The timezone of the location.
 * @param localtime The local time in the specified timezone.
 */
case class LocationInfo(
  country:      Option[String],
  country_code: Option[String],
  city:         Option[String],
  state:        Option[String],
  zipcode:      Option[String],
  latitude:     Option[Float],
  longitude:    Option[Float],
  timezone:     Option[String],
  localtime:    Option[String]
)

/**
 * This case class represents information about the potential risks associated with an IP Address.
 *
 * @param is_mobile Indicates if the IP is associated with a mobile network or not.
 * @param is_vpn Indicates if the IP is using a VPN.
 * @param is_tor Indicates if the IP is part of the TOR network.
 * @param is_proxy Indicates if the IP is using a proxy.
 * @param is_datacenter Indicates if the IP is associated with a data center.
 * @param risk_score A score indicating the risk level (0-100).
 */
case class RiskInfo(
  is_mobile:     Option[Boolean],
  is_vpn:        Option[Boolean],
  is_tor:        Option[Boolean],
  is_proxy:      Option[Boolean],
  is_datacenter: Option[Boolean],
  risk_score:    Option[Int]
)

/**
 * This case class represents the full set of information returned by IPQwery for an IP Address.
 *
 * @param ip The queried IP Address.
 * @param isp Information about the ISP.
 * @param location Information about the location.
 * @param risk Information about the risk level.
 */
case class IPInfo(
  ip:       String,
  isp:      Option[ISPInfo],
  location: Option[LocationInfo],
  risk:     Option[RiskInfo]
)

/**
 * This object helps us parse the JSON output of the API by integrating Circe.
 */
object CirceIntegration:
  /**
   *
   * @param json The JSON input provided from the API.
   * @return Either[io.circe.Error, IPInfo] => Returns an error (left)
   *         or parsed JSON in the form of IPInfo case class.
   */
  def parseJson(json: String): Either[io.circe.Error, IPInfo] =
    decode[IPInfo](json)

/**
 * The main object which includes all the methods for the library.
 */
object IpApi:
  /**
   * Fetches the IP Address of the current machine.
   * @return Either[String, String] => returns error (Left) or the
   *         IP Address of the current machine (Right).
   */
  def queryOwnIP(): Either[String, String] =
    val response: Response[String] = quickRequest
      .get(uri"$BaseURL/")
      .send()

    if (response.is200) {
      Right(response.body)
    } else {
      Left(s"Error: ${response.statusText}")
    }

  /**
   *
   * @param ip The given IP Address information is needed for.
   * @return Either[String, IPInfo] => returns error (Left) or
   *         data in the form of IPInfo (Right).
   */
  def queryIP(ip: String): Either[String, IPInfo] =
    val response: Response[String] = quickRequest
      .get(uri"$BaseURL/$ip")
      .send()

    CirceIntegration.parseJson(response.body) match
      case Right(data) => Right(data)
      case Left(error) => Left(s"Failed to parse JSON: $error")

  // TODO => Make it return: Either[String, List[IPInfo]]
  def queryBulkIP(ips: List[String]): Unit =
    val ip_list = ips.mkString(",")
    val response: Response[String] = quickRequest
      .get(uri"$BaseURL/$ip_list")
      .send()
    // TODO