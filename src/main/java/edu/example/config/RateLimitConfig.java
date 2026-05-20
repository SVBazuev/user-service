package edu.example.config;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@Slf4j
public class RateLimitConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(
            @Value("${app.rate-limit.max-requests:20}") int maxRequests,
            @Value("${app.rate-limit.window-seconds:60}") long windowSeconds) {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimitFilter(maxRequests, windowSeconds));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }

    public static class RateLimitFilter extends OncePerRequestFilter {

        private final int maxRequests;
        private final long windowSeconds;
        private final Cache<String, AtomicInteger> cache;

        public RateLimitFilter(int maxRequests, long windowSeconds) {
            this.maxRequests = maxRequests;
            this.windowSeconds = windowSeconds;
            this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(windowSeconds))
                .maximumSize(10_000)
                .build();
        }

        @Override
        protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            String key = resolveKey(request);
            AtomicInteger counter = cache.get(key, k -> new AtomicInteger(0));
            int count = counter.incrementAndGet();
            if (count > maxRequests) {
                log.warn("Rate limit exceeded for key: {}", key);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write(
                    "Too many requests. Limit: "
                    + maxRequests
                    + " per "
                    + windowSeconds
                    + " seconds."
                );
                return;
            }
            filterChain.doFilter(request, response);
        }

        private String resolveKey(HttpServletRequest request) {
            String auth = request.getHeader("Authorization");
            if (auth != null && !auth.isBlank()) {
                return auth;
            }
            return request.getRemoteAddr();
        }
    }
}
