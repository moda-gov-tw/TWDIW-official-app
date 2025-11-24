package gov.moda.dw.issuer.vc.util;

/**
 * value object 'Tuple', can be used to contain 2 or 3 objects of any type
 * <p>
 * when more than 3 objects, it should consider to use new dedicated class
 *
 * @version 20240902
 */
public class Tuple {

    public static <A, B> Pair<A, B> collect(A a, B b) {
        return new Pair<>(a, b);
    }

    public static <A, B, C> Ternary<A, B, C> collect(A a, B b, C c) {
        return new Ternary<>(a, b, c);
    }

    public static class Pair<A, B> extends Tuple {

        private final A a;
        private final B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof Pair<?,?>) {
                Pair<A, B> pair = (Pair<A, B>) obj;
                return a.equals(pair.getA()) && b.equals(pair.getB());
            }

            return false;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }
    }

    public static class Ternary<A, B, C> extends Tuple {

        private final A a;
        private final B b;
        private final C c;

        public Ternary(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof Ternary<?, ?, ?>) {
                Ternary<A, B, C> ternary = (Ternary<A, B, C>) obj;
                return a.equals(ternary.getA()) && b.equals(ternary.getB()) && c.equals(ternary.getC());
            }

            return false;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }

        public C getC() {
            return c;
        }
    }
}
