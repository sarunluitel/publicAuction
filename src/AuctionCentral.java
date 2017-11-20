/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentral.java - Registers & de-registers auction houses,
 * accepts client requests to participate in auctions, mitigates financial
 * transactions between bank and auction house.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuctionCentral
{
  private static Map<String, AuctionHouse> auctionHouses = Collections.synchronizedMap(new HashMap<String, AuctionHouse>());
  private static String[] houseNames;
}
