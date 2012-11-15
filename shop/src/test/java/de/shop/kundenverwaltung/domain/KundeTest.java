package de.shop.kundenverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class KundeTest extends AbstractDomainTest {
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
}
