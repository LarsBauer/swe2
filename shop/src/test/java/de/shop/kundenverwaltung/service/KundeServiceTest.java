package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractTest;


@RunWith(Arquillian.class)
public class KundeServiceTest extends AbstractTest {
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
}