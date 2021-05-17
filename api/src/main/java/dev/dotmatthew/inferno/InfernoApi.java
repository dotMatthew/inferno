package dev.dotmatthew.inferno;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class InfernoApi {

    private static InfernoApi instance;

    public static InfernoApi getInstance() {
        if(instance == null) {
            instance = new InfernoApi();
        }
        return instance;
    }

}
