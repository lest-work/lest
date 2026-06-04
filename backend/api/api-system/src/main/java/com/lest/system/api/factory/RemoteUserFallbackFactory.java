package com.lest.system.api.factory;

import org.springframework.stereotype.Component;

/**
 * Backward-compatible shim for legacy package name.
 * Delegates to the actual implementation in com.lest.api.system.factory.RemoteUserFallbackFactory.
 */
@Component
public class RemoteUserFallbackFactory extends com.lest.api.system.factory.RemoteUserFallbackFactory {
}
