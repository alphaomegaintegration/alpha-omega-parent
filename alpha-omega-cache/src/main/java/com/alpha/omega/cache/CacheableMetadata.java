package com.alpha.omega.cache;

import com.alpha.omega.cache.expiration.Expiration;

public class CacheableMetadata<T> {
    private String namespace;
    private String key;
    private Expiration expiration;
    private Class<T> clazz;
	private CacheControl cacheControl;

    public CacheableMetadata(String namespace, String key, Expiration expiration, Class<T> clazz) {
        this.namespace = namespace;
        this.key = key;
        this.expiration = expiration;
        this.clazz = clazz;
    }

	public CacheableMetadata(String namespace, String key, Expiration expiration, Class<T> clazz, CacheControl cacheControl) {
		this.namespace = namespace;
		this.key = key;
		this.expiration = expiration;
		this.clazz = clazz;
		this.cacheControl = cacheControl;
	}

	public CacheableMetadata() {
    }


    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setExpiration(Expiration expiration) {
        this.expiration = expiration;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

	public static Builder newBuilder(){
		return new Builder();
	}

	public CacheControl getCacheControl() {
		return cacheControl;
	}

	public void setCacheControl(CacheControl cacheControl) {
		this.cacheControl = cacheControl;
	}

	public static final class Builder {
        private String namespace;
        private String key;
        private Expiration expiration;
        private Class<?> clazz;
		private CacheControl cacheControl;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setExpiration(Expiration expiration) {
            this.expiration = expiration;
            return this;
        }

        public Builder setClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

		public Builder setCacheControl(CacheControl cacheControl) {
			this.cacheControl = cacheControl;
			return this;
		}

		public CacheableMetadata build() {
            CacheableMetadata cacheableMetadata = new CacheableMetadata();
            cacheableMetadata.setNamespace(namespace);
            cacheableMetadata.setKey(key);
            cacheableMetadata.setExpiration(expiration);
            cacheableMetadata.setClazz(clazz);
			cacheableMetadata.setCacheControl(cacheControl);
            return cacheableMetadata;
        }
    }
}
