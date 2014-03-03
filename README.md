redis4j
===================================  
Anther minimalist redis client implementation.

<h3>Simple Benchmark</h3>
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
