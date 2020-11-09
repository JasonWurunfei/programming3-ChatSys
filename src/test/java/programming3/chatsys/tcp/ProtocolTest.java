package programming3.chatsys.tcp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolTest {

    @Test
    void testOK() {
        assertNotNull(Protocol.findMatch("OK"));
    }

    @Test
    void testOKFail() {
        assertNull(Protocol.findMatch("OK "));
        assertNull(Protocol.findMatch("OK "));
        assertNull(Protocol.findMatch(" OK"));
        assertNull(Protocol.findMatch(" OK "));
        assertNull(Protocol.findMatch("ok"));
        assertNull(Protocol.findMatch("Ok"));
    }

    @Test
    void testGetRecentProtocol() {
        Protocol.MatchTuple tuple = Protocol.findMatch("GET recent messages 1");
        assertEquals("GET_RECENT", tuple.type);
        assertEquals("1", tuple.matcher.group("num"));

        tuple = Protocol.findMatch("GET recent messages 10");
        assertEquals("GET_RECENT", tuple.type);
        assertEquals("10", tuple.matcher.group("num"));
    }

    @Test
    void testGetUnreadProtocol() {
        assertNotNull(Protocol.findMatch("GET unread messages"));
    }

    @Test
    void testGetUnreadProtocolFail() {
        assertNull(Protocol.findMatch("GET unread massages"));
        assertNull(Protocol.findMatch("GET unread mesages"));
        assertNull(Protocol.findMatch("GET Unread messages"));
        assertNull(Protocol.findMatch("GET unread messages "));
        assertNull(Protocol.findMatch(" GET unread messages"));
    }

    @Test
    void testPostMessageProtocol() {
        Protocol.MatchTuple tuple = Protocol.findMatch("POST 123");
        assertEquals("POST_MESSAGE", tuple.type);
        assertEquals("123", tuple.matcher.group("message"));

        tuple = Protocol.findMatch("POST Hello world!");
        assertEquals("POST_MESSAGE", tuple.type);
        assertEquals("Hello world!", tuple.matcher.group("message"));
    }

    @Test
    void testPostMessageProtocolFail() {
        assertNull(Protocol.findMatch("POST"));
        assertNull(Protocol.findMatch("POST "));
        assertNull(Protocol.findMatch("Post 123"));
        assertNull(Protocol.findMatch("post 123"));
        assertNull(Protocol.findMatch("POST 123 "));
    }

    @Test
    void testLoginProtocol() {
        Protocol.MatchTuple tuple = Protocol.findMatch("LOGIN Jason 123456");
        assertEquals("LOGIN", tuple.type);
        assertEquals("Jason", tuple.matcher.group("username"));
        assertEquals("123456", tuple.matcher.group("password"));

        tuple = Protocol.findMatch("LOGIN Jane password");
        assertEquals("LOGIN", tuple.type);
        assertEquals("Jane", tuple.matcher.group("username"));
        assertEquals("password", tuple.matcher.group("password"));
    }

    @Test
    void testLoginProtocolFail() {
        assertNull(Protocol.findMatch("LOGIN"));
        assertNull(Protocol.findMatch("LOGIN "));
        assertNull(Protocol.findMatch("LOGIN Jason"));
        assertNull(Protocol.findMatch("LOGIN Jason "));
        assertNull(Protocol.findMatch("Login Jason 123456"));
        assertNull(Protocol.findMatch("login Jason 123456"));
        assertNull(Protocol.findMatch("LOGIN Jason 123456 "));
        assertNull(Protocol.findMatch(" LOGIN Jason 123456"));
    }

    @Test
    void testRegisterProtocol() {
        Protocol.MatchTuple tuple = Protocol.findMatch("REGISTER Jason Jason Wu 123456");
        assertEquals("REGISTER", tuple.type);
        assertEquals("Jason", tuple.matcher.group("username"));
        assertEquals("Jason Wu", tuple.matcher.group("fullName"));
        assertEquals("123456", tuple.matcher.group("password"));

        tuple = Protocol.findMatch("REGISTER Jane JaneDoe password");
        assertEquals("REGISTER", tuple.type);
        assertEquals("Jane", tuple.matcher.group("username"));
        assertEquals("JaneDoe", tuple.matcher.group("fullName"));
        assertEquals("password", tuple.matcher.group("password"));
    }

    @Test
    void testRegisterProtocolFail() {
        assertNull(Protocol.findMatch("REGISTER"));
        assertNull(Protocol.findMatch("REGISTER "));
        assertNull(Protocol.findMatch("REGISTER Jason"));
        assertNull(Protocol.findMatch("REGISTER Jason 123456"));
        assertNull(Protocol.findMatch("REGISTER Jason 123456 "));
        assertNull(Protocol.findMatch("Register Jason JasonWu 123456"));
        assertNull(Protocol.findMatch("register Jason JasonWu 123456"));
        assertNull(Protocol.findMatch(" REGISTER Jason JasonWu 123456"));
        assertNull(Protocol.findMatch("REGISTER Jason JasonWu 123456 "));
        assertNull(Protocol.findMatch(" REGISTER Jason JasonWu 123456 "));
        assertNull(Protocol.findMatch(" REGISTER Jason JasonWu 123456  123"));
    }

}