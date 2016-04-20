package org.apache.commons.math.ode.events;


public class EventStateTest {
	@org.junit.Test
	public void closeEvents() throws org.apache.commons.math.ConvergenceException, org.apache.commons.math.ode.DerivativeException, org.apache.commons.math.ode.events.EventException {
		final double r1 = 90.0;
		final double r2 = 135.0;
		final double gap = r2 - r1;
		org.apache.commons.math.ode.events.EventHandler closeEventsGenerator = new org.apache.commons.math.ode.events.EventHandler() {
			public void resetState(double t, double[] y) {
			}

			public double g(double t, double[] y) {
				return (t - r1) * (r2 - t);
			}

			public int eventOccurred(double t, double[] y, boolean increasing) {
				return org.apache.commons.math.ode.events.EventHandler.CONTINUE;
			}
		};
		final double tolerance = 0.1;
		org.apache.commons.math.ode.events.EventState es = new org.apache.commons.math.ode.events.EventState(closeEventsGenerator , (1.5 * gap) , tolerance , 10);
		double t0 = r1 - (0.5 * gap);
		es.reinitializeBegin(t0, new double[0]);
		org.apache.commons.math.ode.sampling.AbstractStepInterpolator interpolator = new org.apache.commons.math.ode.sampling.DummyStepInterpolator(new double[0] , new double[0] , true);
		interpolator.storeTime(t0);
		interpolator.shift();
		interpolator.storeTime((0.5 * (r1 + r2)));
		junit.framework.Assert.assertTrue(es.evaluateStep(interpolator));
		junit.framework.Assert.assertEquals(r1, es.getEventTime(), tolerance);
		es.stepAccepted(es.getEventTime(), new double[0]);
		interpolator.shift();
		interpolator.storeTime((r2 + (0.4 * gap)));
		junit.framework.Assert.assertTrue(es.evaluateStep(interpolator));
		junit.framework.Assert.assertEquals(r2, es.getEventTime(), tolerance);
	}
}

