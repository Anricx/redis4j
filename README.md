redis4j
===================================  
Anther minimalist redis client implementation.

<h3>Features</h3>
COMMAND implemented:
<pre>
AUTH,	// Request for authentication in a password-protected Redis server.
SELECT,	// Select the DB with having the specified zero-based numeric index.
SET,	// Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type.
GET,	// Get the value of key. If the key does not exist the special value nil is returned.
APPEND,	// If key already exists and is a string, this command appends the value at the end of the string.
DEL,	// Removes the specified keys. A key is ignored if it does not exist.
EXPIRE, // Set a timeout on key. After the timeout has expired, the key will automatically be deleted.
EXISTS,	// Check if key exists.
FLUSHDB,// Delete all the keys of the currently selected DB. This command never fails.
ECHO,	// Returns message.
PING,	// This command is often used to test if a connection is still alive, or to measure latency.
QUIT	// Ask the server to close the connection.
</pre>

<h3>Simple Benchmark</h3>
<pre>
10000 set requests.
single thread task runner.
more in https://github.com/Mailerm/redis4j/blob/master/com.gmail.dengtao.joe.redis4j/test/com/gmail/dengtao/joe/redis4j/RedisTest.java
</pre>
<pre>
......
SET OiiOXPCHASDaULgE5pIjxWZj4wNvG2fY1RDR=>m2SKZid2uAqi in 321477 ns
SET YpPCXIP2TmM8utxNQX4Kh4l4ejRaEdE1uAxR=>LtMQy29kcHYs in 320270 ns
SET xX6yxP9rN7Z9w07UF2KGxQAZzDYTHMbjQ0Hr=>orJoRE4f5mg6 in 319666 ns
SET T2PyWyJG7cLRCNBTzL08r2g6efIDVGZv3hS7=>Bxh5aWekjfwq in 324798 ns
SET YI0pfqnlN4GeiegCr3WPpPr5jaE6xHJQ7DSs=>MCMPw0JWvELq in 320873 ns
SET fFHM59KcObgHIh4SftiaRzEReIr4BfRkYPZS=>iNb3GKYNYLge in 351059 ns
SET vnKtYLjYoJ4Peo4YNC7xHIdJPker3alM4Z5a=>QFczUIJgIEcs in 355888 ns
SET iMH3jrhvbaTGE14WKwnIfjmfSEaJj5yYk5Wg=>1ru7HOuFLsrs in 355285 ns
.......
All Down!
Total: 3677573754ns, Avg:0.3677573754ms
</pre>

Single Thread in deep! not support database pool! if you need, try to do it youself!
