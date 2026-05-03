<?php
declare(strict_types=1);

namespace App\Tests\Unit;

use App\Cache\RedisCache;
use PHPUnit\Framework\TestCase;
use Predis\Client;

final class RedisCacheTest extends TestCase {
    public function testRememberJsonUsesLoaderWhenCacheMiss(): void {
        $client = $this->getMockBuilder(Client::class)
            ->disableOriginalConstructor()
            ->onlyMethods(['__call'])
            ->getMock();

        $client->expects($this->exactly(2))->method('__call')->willReturnCallback(function (string $command, array $args) {
            return match (strtoupper($command)) {
                'GET' => null,
                'SETEX' => null,
                default => null,
            };
        });

        $cache = new RedisCache($client);
        $value = $cache->rememberJson('test:key', 60, static fn () => ['a' => 1]);
        $this->assertSame(['a' => 1], $value);
    }

    public function testRememberJsonReturnsCachedValueWithoutCallingLoader(): void {
        $client = $this->getMockBuilder(Client::class)
            ->disableOriginalConstructor()
            ->onlyMethods(['__call'])
            ->getMock();

        $client->expects($this->once())->method('__call')->with(
            'get',
            ['distcomp:k']
        )->willReturn('{"x":"y"}');

        $cache = new RedisCache($client);
        $called = false;
        $value = $cache->rememberJson('k', 10, function () use (&$called) {
            $called = true;
            return ['should' => 'not'];
        });
        $this->assertSame(['x' => 'y'], $value);
        $this->assertFalse($called);
    }

    public function testInvalidateNoticeDeletesExpectedKeys(): void {
        $client = $this->getMockBuilder(Client::class)
            ->disableOriginalConstructor()
            ->onlyMethods(['__call'])
            ->getMock();

        $call = 0;
        $client->expects($this->exactly(2))->method('__call')->willReturnCallback(function (string $command, array $args) use (&$call) {
            $this->assertSame('del', strtolower($command));
            $call++;
            if ($call === 1) {
                $this->assertSame([['distcomp:v1.0:notice:5']], $args);
            } else {
                $this->assertSame([['distcomp:v1.0:notices:list']], $args);
            }
            return 1;
        });

        $cache = new RedisCache($client);
        $cache->invalidateNotice('v1.0', 5);
    }
}
