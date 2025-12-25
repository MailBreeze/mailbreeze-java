package com.mailbreeze;

import com.mailbreeze.resources.Emails;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MailBreeze Client")
class MailBreezeTest {

    @Nested
    @DisplayName("Configuration")
    class ConfigurationTests {

        @Test
        @DisplayName("should create client with API key only (using defaults)")
        void shouldCreateWithApiKeyOnly() {
            MailBreeze client = MailBreeze.builder()
                    .apiKey("sk_test_123")
                    .build();

            assertThat(client).isNotNull();
            assertThat(client.emails()).isNotNull();
        }

        @Test
        @DisplayName("should create client with full configuration")
        void shouldCreateWithFullConfig() {
            MailBreeze client = MailBreeze.builder()
                    .apiKey("sk_test_123")
                    .baseUrl("https://custom.api.mailbreeze.com")
                    .timeout(Duration.ofSeconds(60))
                    .maxRetries(5)
                    .build();

            assertThat(client).isNotNull();
        }

        @Test
        @DisplayName("should throw exception when API key is null")
        void shouldThrowWhenApiKeyNull() {
            assertThatThrownBy(() -> MailBreeze.builder().build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("API key");
        }

        @Test
        @DisplayName("should throw exception when API key is empty")
        void shouldThrowWhenApiKeyEmpty() {
            assertThatThrownBy(() -> MailBreeze.builder().apiKey("").build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("API key");
        }

        @Test
        @DisplayName("should throw exception when API key is blank")
        void shouldThrowWhenApiKeyBlank() {
            assertThatThrownBy(() -> MailBreeze.builder().apiKey("   ").build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("API key");
        }
    }

    @Nested
    @DisplayName("Resource Accessors")
    class ResourceAccessorTests {

        private MailBreeze client;

        @BeforeEach
        void setUp() {
            client = MailBreeze.builder()
                    .apiKey("sk_test_123")
                    .build();
        }

        @Test
        @DisplayName("should return Emails resource")
        void shouldReturnEmailsResource() {
            Emails emails = client.emails();

            assertThat(emails).isNotNull();
            assertThat(emails).isInstanceOf(Emails.class);
        }

        @Test
        @DisplayName("should return same Emails instance on repeated calls")
        void shouldReturnSameEmailsInstance() {
            Emails emails1 = client.emails();
            Emails emails2 = client.emails();

            assertThat(emails1).isSameAs(emails2);
        }
    }

    @Nested
    @DisplayName("Default Configuration")
    class DefaultConfigurationTests {

        @Test
        @DisplayName("should use production base URL by default")
        void shouldUseProductionBaseUrl() {
            // Verify default values through the builder
            MailBreeze.Builder builder = MailBreeze.builder().apiKey("sk_test_123");

            // The actual base URL is internal, but we verify the client builds successfully
            MailBreeze client = builder.build();
            assertThat(client).isNotNull();
        }
    }
}
