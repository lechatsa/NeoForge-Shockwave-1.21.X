package net.ocechat.shockwave.utility;

import net.ocechat.shockwave.ShockwaveMod;

import static net.ocechat.shockwave.ShockwaveConfig.BASE_VALUE_MAX;
import static net.ocechat.shockwave.ShockwaveConfig.BASE_VALUE_ZERO;

public record ExplosionFormula(Double distanceForZero, Double maxDamage) {

    // -- damage (HP) :
    public static float calculateDamage(double x, int numberOfTNT, ExplosionFormula formula) {

        double squareTNT = Math.pow(numberOfTNT, 1.0/3.0);
        double a = squareTNT; // a is the multiplier of the MAX damage dealt at 0m
        double b = squareTNT; // b is the multiplier of the distance at which the damage is null

        double p = (formula.maxDamage != null) ? formula.maxDamage : BASE_VALUE_MAX.get();
        double m = (formula.distanceForZero != null) ? -p / Math.sqrt(formula.distanceForZero) : -p / Math.sqrt(BASE_VALUE_ZERO.get());

        double damage = Math.max(m * (a)/(Math.sqrt(b)) * Math.sqrt(x) + p * a, 0.0); // dmg(x) = m * (a)/(sqrt(b)) sqrt(x) + p * a
        if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (ShockwaveModule) f({}) = {} x {}/ √{} x √{} + {} x {} = {}dmg", x, m, a, b, x, p, a, Math.round(damage));
        return (float) damage;
    }

    // -- damage (HP) :
    public static float calculateDamage(double x, ExplosionFormula formula, double a, double b) {

        double p = (formula.maxDamage != null) ? formula.maxDamage : BASE_VALUE_MAX.get();
        double m = (formula.distanceForZero != null) ? -p / Math.sqrt(formula.distanceForZero) : -p / Math.sqrt(BASE_VALUE_ZERO.get());

        double damage = Math.max(m * (a)/(Math.sqrt(b)) * Math.sqrt(x) + p * a, 0.0); // dmg(x) = m * (a)/(sqrt(b)) sqrt(x) + p * a
        if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (ShockwaveModule) f({}) = {} x {}/ √{} x √{} + {} x {} = {}dmg", x, m, a, b, x, p, a, Math.round(damage));
        return (float) damage;
    }

    // -- damage (HP) :
    public static float calculateDamage(double x, int numberOfTNT) {

        double squareTNT = Math.pow(numberOfTNT, 1.0/3.0);
        double a = squareTNT; // a is the multiplier of the MAX damage dealt at 0m
        double b = squareTNT; // b is the multiplier of the distance at which the damage is null

        double p = BASE_VALUE_MAX.get();
        double m = -p / Math.sqrt(BASE_VALUE_ZERO.get());

        double damage = Math.max(m * (a)/(Math.sqrt(b)) * Math.sqrt(x) + p * a, 0.0); // dmg(x) = m * (a)/(sqrt(b)) sqrt(x) + p * a
        if (ShockwaveMod.DEBUG) ShockwaveMod.LOGGER.info("[Shockwave] (ShockwaveModule) f({}) = {} x {}/ √{} x √{} + {} x {} = {}dmg", x, m, a, b, x, p, a, Math.round(damage));
        return (float) damage;
    }

}
