package org.faust.base;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class E2ETestExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        String uniqueKey = this.getClass().getName();

        if (!started) {
            started = true;
            E2ETestBase.setUp();
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(uniqueKey, this);
        }
    }

    @Override
    public void close() {
        E2ETestBase.tearDown();
    }
}
