package com.example.plugin.cevemarketDTO;

public class MarketResult
{
    MarketData all;
    MarketData buy;
    MarketData sell;

    public MarketData getAll()
    {
        return all;
    }

    public void setAll(MarketData all)
    {
        this.all = all;
    }

    public MarketData getBuy()
    {
        return buy;
    }

    public void setBuy(MarketData buy)
    {
        this.buy = buy;
    }

    public MarketData getSell()
    {
        return sell;
    }

    public void setSell(MarketData sell)
    {
        this.sell = sell;
    }
}
