package edu.coursera.concurrent;

import static edu.rice.pcdp.PCDP.*;

import edu.rice.pcdp.Actor;
import java.util.ArrayList;
import java.util.List;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        final SieveActorActor actor = new SieveActorActor(2);

        finish(() -> {
            for (int i = 3; i <= limit; i++) {
                actor.send(i);
            }
        });

        int result = 0;
        SieveActorActor current = actor;
        while (current != null) {
            result += current.localPrimes.size();
            current = current.nextActor;
        }

        return result;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */

        static final int MAX_LOCAL_PRIMES = 1000;
        SieveActorActor nextActor = null;
        List<Integer> localPrimes = new ArrayList<>();

        SieveActorActor(final int localPrime) {
            localPrimes.add(localPrime);
        }

        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;

            if (isLocallyPrime(candidate)) {
                if (localPrimes.size() < MAX_LOCAL_PRIMES) {
                    localPrimes.add(candidate);
                } else if (nextActor == null) {
                    nextActor = new SieveActorActor(candidate);
                } else {
                    nextActor.send(candidate);
                }
            }
        }

        private boolean isLocallyPrime(final int candidate) {
            for (int n : localPrimes) {
                if (candidate % n == 0) {
                    return false;
                }
            }

            return true;
        }
    }
}
