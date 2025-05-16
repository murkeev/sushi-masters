package murkeev;

import murkeev.config.B2S3Config;
import murkeev.security.JwtTokenUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockedExternalConfig {

    @Bean
    @Primary
    public B2S3Config b2S3Config() {
        return Mockito.mock(B2S3Config.class);
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return Mockito.mock(JwtTokenUtil.class);
    }
}
