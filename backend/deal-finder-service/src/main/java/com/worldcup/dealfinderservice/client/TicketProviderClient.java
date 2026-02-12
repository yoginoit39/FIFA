package com.worldcup.dealfinderservice.client;

import com.worldcup.dealfinderservice.entity.PriceSnapshot;

import java.util.List;

public interface TicketProviderClient {

    String getProviderName();

    List<PriceSnapshot> fetchPrices(String keyword);
}
