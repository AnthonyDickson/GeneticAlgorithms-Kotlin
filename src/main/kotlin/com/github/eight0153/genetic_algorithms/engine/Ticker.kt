package com.github.eight0153.genetic_algorithms.engine


interface TickerSubscriberI {
    fun onTick()
}

/** Emits a 'onTick' event periodically. */
class Ticker(
    /** The amount of time (seconds) between ticks, */
    private val tickerInterval: Double = 1.0
) {
    private var timeSinceLastTick = 0.0
    private val subscribers = ArrayList<TickerSubscriberI>()

    fun subscribe(subscriber: TickerSubscriberI) {
        subscribers.add(subscriber)
    }

    fun unsubscribe(subscriber: TickerSubscriberI) {
        subscribers.remove(subscriber)
    }

    fun update(delta: Double) {
        timeSinceLastTick += delta

        if (timeSinceLastTick >= tickerInterval) {
            timeSinceLastTick = 0.0
            subscribers.forEach { it.onTick() }
        }
    }
}