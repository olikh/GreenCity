package greencity.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.BadIdException;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.repository.VerifyEmailRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class VerifyEmailServiceImplTest {

    @Mock private VerifyEmailRepo repo;

    private VerifyEmailServiceImpl verifyEmailService;

    @Before
    public void init(){
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        verifyEmailService = new VerifyEmailServiceImpl(repo, javaMailSender);
    }


    @Test
    public void save(){
        ReflectionTestUtils.setField(verifyEmailService, "expireTime", 5);
        when(repo.save(any(VerifyEmail.class))).thenReturn(null);
        verifyEmailService.save(User.builder().email("").firstName("").build());

        verify(repo, (times(1))).save(any(VerifyEmail.class));
    }

    @Test
    public void verifyByEmail() {
        VerifyEmail verifyEmail =
                VerifyEmail.builder().expiryDate(LocalDateTime.now().plusHours(2)).id(2L).build();
        when(repo.findByToken(anyString())).thenReturn(Optional.of(verifyEmail));
        when(repo.existsById(anyLong())).thenReturn(true);
        doNothing().when(repo).delete(any(VerifyEmail.class));
        verifyEmailService.verifyByToken("some token");
        verify(repo, times(1)).delete(any());
    }

    @Test(expected = UserActivationEmailTokenExpiredException.class)
    public void verifyIsNotActive() {
        VerifyEmail verifyEmail =
                VerifyEmail.builder().expiryDate(LocalDateTime.now().minusHours(2)).build();
        when(repo.findByToken(anyString())).thenReturn(Optional.of(verifyEmail));
        verifyEmailService.verifyByToken("some token");
    }

    @Test
    public void isDateValidate() {
        assertTrue(verifyEmailService.isDateValidate(LocalDateTime.now().plusHours(24)));
        assertFalse(verifyEmailService.isDateValidate(LocalDateTime.now().minusHours(48)));
    }



    @Test(expected = BadIdException.class)
    public void delete() {
        when(repo.existsById(anyLong())).thenReturn(false);
        verifyEmailService.delete(VerifyEmail.builder().id(1L).build());
    }

    @Test
    public void findAll() {
        List<VerifyEmail> verifyEmails = Collections.singletonList(new VerifyEmail());
        when(repo.findAll()).thenReturn(verifyEmails);

        assertEquals(verifyEmailService.findAll(), verifyEmails);
    }
}