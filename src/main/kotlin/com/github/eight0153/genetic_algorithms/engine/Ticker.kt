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
    private val newSubscribers = ArrayList<TickerSubscriberI>()
    private val unsubscribers = ArrayList<TickerSubscriberI>()

    fun subscribe(subscriber: TickerSubscriberI) {
        newSubscribers.add(subscriber)
    }

    fun unsubscribe(subscriber: TickerSubscriberI) {
        unsubscribers.add(subscriber)
    }

    fun update(delta: Double) {
        timeSinceLastTick += delta

        // Update subscriber list here (rather than in subscribe() and unsubscribe()) to avoid concurrent modification
        // errors.
        subscribers.addAll(newSubscribers)
        subscribers.removeAll(unsubscribers)
        newSubscribers.clear()
        unsubscribers.clear()

        if (timeSinceLastTick >= tickerInterval) {
            timeSinceLastTick = 0.0
            subscribers.forEach { it.onTick() }
        }
    }
}