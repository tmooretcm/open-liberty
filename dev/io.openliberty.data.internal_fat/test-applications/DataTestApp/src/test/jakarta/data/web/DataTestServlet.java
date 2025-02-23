/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package test.jakarta.data.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static test.jakarta.data.web.Assertions.assertArrayEquals;
import static test.jakarta.data.web.Assertions.assertIterableEquals;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import org.junit.Test;

import componenttest.app.FATServlet;
import io.openliberty.data.Page;
import io.openliberty.data.Pagination;
import io.openliberty.data.Sort;
import io.openliberty.data.Sorts;

@SuppressWarnings("serial")
@WebServlet("/*")
public class DataTestServlet extends FATServlet {
    private final long TIMEOUT_MINUTES = 2;

    @Inject
    OrderRepo orders;

    @Inject
    PersonRepo people;

    @Inject
    Personnel personnel;

    @Inject
    ProductRepo products;

    @Inject
    Reservations reservations;

    @Inject
    Shipments shipments;

    @Inject
    ShippingAddresses shippingAddresses;

    @Inject
    Tariffs tariffs;

    @Resource
    private UserTransaction tran;

    /**
     * Use repository methods that are designated as asynchronous by the Concurrency Asynchronous annotation.
     */
    @Test
    public void testAsynchronous() throws ExecutionException, InterruptedException, TimeoutException {
        // Clear out old data before running test
        CompletableFuture<Long> deleted = personnel.removeAll();
        deleted.get(TIMEOUT_MINUTES, TimeUnit.MINUTES);

        // Data for test to use:
        Person p1 = new Person();
        p1.firstName = "Aaron";
        p1.lastName = "TestAsynchronous";
        p1.ssn = 1002003001;

        Person p2 = new Person();
        p2.firstName = "Amy";
        p2.lastName = "TestAsynchronous";
        p2.ssn = 1002003002;

        Person p3 = new Person();
        p3.firstName = "Alice";
        p3.lastName = "TestAsynchronous";
        p3.ssn = 1002003003;

        Person p4 = new Person();
        p4.firstName = "Alexander";
        p4.lastName = "TestAsynchronous";
        p4.ssn = 1002003004;

        Person p5 = new Person();
        p5.firstName = "Andrew";
        p5.lastName = "TestAsynchronous";
        p5.ssn = 1002003005;

        Person p6 = new Person();
        p6.firstName = "Brian";
        p6.lastName = "TestAsynchronous";
        p6.ssn = 1002003006;

        Person p7 = new Person();
        p7.firstName = "Betty";
        p7.lastName = "TestAsynchronous";
        p7.ssn = 1002003007;

        Person p8 = new Person();
        p8.firstName = "Bob";
        p8.lastName = "TestAsynchronous";
        p8.ssn = 1002003008;

        Person p9 = new Person();
        p9.firstName = "Albert";
        p9.lastName = "TestAsynchronous";
        p9.ssn = 1002003009;

        Person p10 = new Person();
        p10.firstName = "Ben";
        p10.lastName = "TestAsynchronous";
        p10.ssn = 1002003010;

        // Async multiple insert
        CompletableFuture<List<Person>> added = personnel.save(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

        assertIterableEquals(List.of("Aaron", "Amy", "Alice", "Alexander", "Andrew", "Brian", "Betty", "Bob", "Albert", "Ben"),
                             added.get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                                             .stream()
                                             .map(p -> p.firstName)
                                             .collect(Collectors.toList()));

        // Async update
        CompletionStage<List<Person>> updated = personnel.changeSurnames("TestAsynchronous", "Test-Asynchronous",
                                                                         List.of(1002003009L, 1002003008L, 1002003005L,
                                                                                 1002003003L, 1002003002L, 1002003001L))
                        .thenCompose(updateCount -> {
                            assertEquals(Long.valueOf(6), updateCount);

                            return personnel.findByLastNameOrderByFirstName("Test-Asynchronous");
                        });

        assertIterableEquals(List.of("Aaron", "Albert", "Alice", "Amy", "Andrew", "Bob"),
                             updated.toCompletableFuture()
                                             .get(TIMEOUT_MINUTES, TimeUnit.MINUTES)
                                             .stream()
                                             .map(p -> p.firstName)
                                             .collect(Collectors.toList()));

        // Async find with Consumer
        LinkedBlockingQueue<String> names = new LinkedBlockingQueue<>();
        personnel.findByLastNameOrderByFirstNameDesc("TestAsynchronous", name -> names.add(name));

        assertEquals("Brian", names.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals("Betty", names.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals("Ben", names.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals("Alexander", names.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));

        // Paginated async find with Consumer and CompletableFuture to track completion
        Queue<Long> ids = new LinkedList<Long>();
        CompletableFuture<Void> allFound = personnel.findByOrderBySsnDesc(p -> ids.add(p.ssn));

        assertEquals(null, allFound.get(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(Long.valueOf(p10.ssn), ids.poll());
        assertEquals(Long.valueOf(p9.ssn), ids.poll());
        assertEquals(Long.valueOf(p8.ssn), ids.poll());
        assertEquals(Long.valueOf(p7.ssn), ids.poll());
        assertEquals(Long.valueOf(p6.ssn), ids.poll());
        assertEquals(Long.valueOf(p5.ssn), ids.poll());
        assertEquals(Long.valueOf(p4.ssn), ids.poll());
        assertEquals(Long.valueOf(p3.ssn), ids.poll());
        assertEquals(Long.valueOf(p2.ssn), ids.poll());
        assertEquals(Long.valueOf(p1.ssn), ids.poll());

        // Async find single item
        CompletableFuture<Person> future = personnel.findBySsn(p4.ssn);

        Person p = future.get(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        assertNotNull(p);
        assertEquals(p4.ssn, p.ssn);
        assertEquals(p4.firstName, p.firstName);
        assertEquals(p4.lastName, p.lastName);

        // Async find with Collector

        // Have a collector reduce the results to a count of names.
        // The database could have done this instead, but it makes a nice, simple example.
        CompletableFuture<Long> countOfANames = personnel.namesThatStartWith("A", Collectors.counting());

        assertEquals(Long.valueOf(6), countOfANames.get(TIMEOUT_MINUTES, TimeUnit.MINUTES));

        // Have a collector reduce the results to the length of the longest name found.
        Collector<String, ?, Long> maxLengthFinder = Collectors.collectingAndThen(
                                                                                  Collectors.maxBy(Comparator.<String, Integer> comparing(n -> n.length())),
                                                                                  n -> n.isPresent() ? n.get().length() : -1L);

        CompletableFuture<Long> maxLengthOfAnyAName = personnel.namesThatStartWith("A", maxLengthFinder);

        assertEquals(Long.valueOf(9), maxLengthOfAnyAName.get(TIMEOUT_MINUTES, TimeUnit.MINUTES));

        // Have a collector reduce the results to an average name length,
        final int COUNT = 0, SUM = 1;
        Collector<String, ?, Long> lengthAverager = Collector.of(() -> new long[2],
                                                                 (len, name) -> {
                                                                     len[COUNT]++;
                                                                     len[SUM] += name.length();
                                                                 },
                                                                 (left, right) -> new long[] {
                                                                                               left[COUNT] + right[COUNT],
                                                                                               left[SUM] + right[SUM]
                                                                 },
                                                                 len -> len[COUNT] == 0 ? 0 : (len[SUM] / len[COUNT]));

        CompletableFuture<Long> avgLengthOfBNames = personnel.namesThatStartWith("B", lengthAverager);

        assertEquals(Long.valueOf(4), avgLengthOfBNames.get(TIMEOUT_MINUTES, TimeUnit.MINUTES));

        deleted = personnel.removeAll();
        assertEquals(Long.valueOf(10), deleted.get(TIMEOUT_MINUTES, TimeUnit.MINUTES));
    }

    /**
     * Verify that repository methods that are designated to run asynchronously
     * actually do run asynchronously by performing database operations that would
     * deadlock if not run asynchronously.
     */
    @Test
    public void testAsyncPreventsDeadlock() throws ExecutionException, InterruptedException, NotSupportedException, SystemException, TimeoutException {
        // Clear out old data before running test
        CompletableFuture<Long> deleted = personnel.removeAll();
        deleted.get(TIMEOUT_MINUTES, TimeUnit.MINUTES);

        // Data for test to use:
        Person p1 = new Person();
        p1.firstName = "Christopher";
        p1.lastName = "TestAsyncPreventsDeadlock";
        p1.ssn = 1001001001;

        Person p2 = new Person();
        p2.firstName = "Chad";
        p2.lastName = "TestAsyncPreventsDeadlock";
        p2.ssn = 2002002002;

        // Async multiple insert
        CompletableFuture<List<Person>> added = personnel.save(p1, p2);

        assertEquals(2, added.get(TIMEOUT_MINUTES, TimeUnit.MINUTES).size());

        CompletableFuture<Long> updated2Then1;
        CompletableFuture<Long> updated2;

        tran.begin();
        try {
            // main thread obtains lock on p1
            assertEquals(1L, personnel.setSurname("Test-AsyncPreventsDeadlock", p1.ssn));

            CountDownLatch locked2 = new CountDownLatch(1);

            // second thread obtains lock on p2 and then attempts lock on p1
            updated2Then1 = added.thenApplyAsync(a -> {
                try {
                    tran.begin();
                    try {
                        // lock on p2
                        long updateCount2 = personnel.setSurname("TestAsync-PreventsDeadlock", p2.ssn);

                        locked2.countDown();

                        // lock on p1
                        return updateCount2 + personnel.setSurname("TestAsync-PreventsDeadlock", p1.ssn);
                    } finally {
                        tran.rollback();
                    }
                } catch (Exception x) {
                    throw new CompletionException(x);
                }
            });

            assertEquals(true, locked2.await(TIMEOUT_MINUTES, TimeUnit.MINUTES));

            // If this runs on a third thread as expected, it will be blocked until the second thread releases the lock.
            // If it runs inline (unexpected) deadlock will occur.
            updated2 = personnel.setSurnameAsync("TestAsyncPrevents-Deadlock", p2.ssn);

            try {
                Long updateCount = updated2.get(1, TimeUnit.SECONDS);
                fail("Third thread ought to be blocked by second thread. Instead, updated " + updateCount);
            } catch (TimeoutException x) {
                // expected
            }
        } finally {
            // release lock on p1
            tran.rollback();
        }

        // With the lock on p1 released, the second thread can obtain that lock and complete
        assertEquals(Long.valueOf(2), updated2Then1.get(TIMEOUT_MINUTES, TimeUnit.MINUTES));

        // With the second thread completing, it releases both locks, allowing the third thread to obtain the lock on 2 and complete
        assertEquals(Long.valueOf(1), updated2.get(TIMEOUT_MINUTES, TimeUnit.MINUTES));
    }

    /**
     * Delete multiple entries.
     */
    @Test
    public void testDeleteMultiple() {
        Product prod1 = new Product();
        prod1.id = "TDM-SE";
        prod1.name = "TestDeleteMultiple Standard Edition";
        prod1.price = 115.99f;
        products.insert(prod1);

        Product prod2 = new Product();
        prod2.id = "TDM-AE";
        prod2.name = "TestDeleteMultiple Advanced Edition";
        prod2.price = 197.99f;
        products.insert(prod2);

        Product prod3 = new Product();
        prod3.id = "TDM-EE";
        prod3.name = "TestDeleteMultiple Expanded Edition";
        prod3.price = 153.99f;
        products.insert(prod3);

        Product prod4 = new Product();
        prod4.id = "TDM-NFE";
        prod4.name = "TestDeleteMultiple Nearly Free Edition";
        prod4.price = 1.99f;
        products.insert(prod4);

        assertEquals(2, products.discontinueProducts(Set.of("TDM-AE", "TDM-NFE", "TDM-NOT-FOUND")));

        // expect that 2 remain
        assertNotNull(products.findItem("TDM-SE"));
        assertNotNull(products.findItem("TDM-EE"));
    }

    /**
     * Add, search, and remove entities with Embeddable fields.
     */
    @Test
    public void testEmbeddable() {
        shippingAddresses.removeAll();

        ShippingAddress a1 = new ShippingAddress();
        a1.id = 1001L;
        a1.city = "Rochester";
        a1.state = "Minnesota";
        a1.streetAddress = new StreetAddress(2800, "37th St NW");
        a1.zipCode = 55901;
        shippingAddresses.save(a1);

        ShippingAddress a2 = new ShippingAddress();
        a2.id = 1002L;
        a2.city = "Rochester";
        a2.state = "Minnesota";
        a2.streetAddress = new StreetAddress(201, "4th St SE");
        a2.zipCode = 55904;
        shippingAddresses.save(a2);

        ShippingAddress a3 = new ShippingAddress();
        a3.id = 1003L;
        a3.city = "Rochester";
        a3.state = "Minnesota";
        a3.streetAddress = new StreetAddress(200, "1st Ave SW");
        a3.zipCode = 55902;
        shippingAddresses.save(a3);

        ShippingAddress a4 = new ShippingAddress();
        a4.id = 1004L;
        a4.city = "Rochester";
        a4.state = "Minnesota";
        a4.streetAddress = new StreetAddress(151, "4th St SE");
        a4.zipCode = 55904;
        shippingAddresses.save(a4);

        assertArrayEquals(new ShippingAddress[] { a4, a2 },
                          shippingAddresses.findByStreetNameOrderByHouseNumber("4th St SE"),
                          Comparator.<ShippingAddress, Long> comparing(o -> o.id)
                                          .thenComparing(Comparator.<ShippingAddress, String> comparing(o -> o.city))
                                          .thenComparing(Comparator.<ShippingAddress, String> comparing(o -> o.state))
                                          .thenComparing(Comparator.<ShippingAddress, Integer> comparing(o -> o.streetAddress.houseNumber))
                                          .thenComparing(Comparator.<ShippingAddress, String> comparing(o -> o.streetAddress.streetName))
                                          .thenComparing(Comparator.<ShippingAddress, Integer> comparing(o -> o.zipCode)));

        assertIterableEquals(List.of("200 1st Ave SW", "151 4th St SE", "201 4th St SE"),
                             Stream.of(shippingAddresses.findByHouseNumberBetweenOrderByStreetNameOrderByHouseNumber(150, 250))
                                             .map(a -> a.houseNumber + " " + a.streetName)
                                             .collect(Collectors.toList()));

        assertEquals(4, shippingAddresses.removeAll());
    }

    /**
     * Search for missing item. Insert it. Search again.
     */
    @Test
    public void testFindCreateFind() {
        assertEquals(null, products.findItem("OL306-233F"));

        Product prod = new Product();
        prod.id = "OL306-233F";
        prod.name = "Something";
        prod.price = 3.99f;
        prod.description = "An item for sale.";

        products.insert(prod);

        Product p = products.findItem("OL306-233F");
        assertEquals(prod.id, p.id);
        assertEquals(prod.name, p.name);
        assertEquals(prod.price, p.price, 0.001f);
        assertEquals(prod.description, p.description);
    }

    /**
     * Search for multiple entries.
     */
    @Test
    public void testFindMultiple() throws Exception {
        assertEquals(Collections.EMPTY_LIST, people.find("TestFindMultiple"));

        Person jane = new Person();
        jane.firstName = "Jane";
        jane.lastName = "TestFindMultiple";
        jane.ssn = 123456789;

        Person joe = new Person();
        joe.firstName = "Joe";
        joe.lastName = "TestFindMultiple";
        joe.ssn = 987654321;

        tran.begin();
        try {
            people.insert(jane);
            people.insert(joe);
        } finally {
            if (tran.getStatus() == Status.STATUS_MARKED_ROLLBACK)
                tran.rollback();
            else
                tran.commit();
        }

        List<Person> found = people.find("TestFindMultiple");
        assertNotNull(found);
        assertEquals(2, found.size());

        Person p1 = found.get(0);
        Person p2expected;
        assertEquals("TestFindMultiple", p1.lastName);
        if (jane.firstName.equals(p1.firstName)) {
            assertEquals(jane.ssn, p1.ssn);
            p2expected = joe;
        } else {
            assertEquals(joe.ssn, p1.ssn);
            p2expected = jane;
        }

        Person p2 = found.get(1);
        assertEquals(p2expected.lastName, p2.lastName);
        assertEquals(p2expected.firstName, p2.firstName);
        assertEquals(p2expected.ssn, p2.ssn);
    }

    /**
     * Avoid specifying a primary key value and let it be generated.
     */
    @Test
    public void testGeneratedKey() {
        ZoneOffset MDT = ZoneOffset.ofHours(-6);

        Order o1 = new Order();
        o1.purchasedBy = "testGeneratedKey-Customer1";
        o1.purchasedOn = OffsetDateTime.of(2022, 6, 1, 9, 30, 0, 0, MDT);
        o1.total = 25.99f;
        o1 = orders.save(o1);

        Order o2 = new Order();
        o2.purchasedBy = "testGeneratedKey-Customer2";
        o2.purchasedOn = OffsetDateTime.of(2022, 6, 1, 14, 0, 0, 0, MDT);
        o2.total = 148.98f;
        o2 = orders.save(o2);

        assertNotNull(o1.id);
        assertNotNull(o2.id);
        assertEquals(false, o1.id.equals(o2.id));

        assertEquals(true, orders.addTaxAndShipping(o2.id, 1.08f, 7.99f));

        o2 = orders.findById(o2.id).get();

        assertEquals(168.89f, o2.total, 0.01f);
    }

    /**
     * Add, find, and remove entities with a mapped superclass.
     * Also tests automatically paginated iterator and list.
     */
    @Test
    public void testMappedSuperclass() {
        tariffs.deleteByLeviedBy("USA");

        Tariff t1 = new Tariff();
        t1.leviedAgainst = "China";
        t1.leviedBy = "USA";
        t1.leviedOn = "Solar Panels";
        t1.rate = 0.15f;
        tariffs.save(t1);

        Tariff t2 = new Tariff();
        t2.leviedAgainst = "Germany";
        t2.leviedBy = "USA";
        t2.leviedOn = "Steel";
        t2.rate = 0.25f;
        tariffs.save(t2);

        Tariff t3 = new Tariff();
        t3.leviedAgainst = "India";
        t3.leviedBy = "USA";
        t3.leviedOn = "Aluminum";
        t3.rate = 0.1f;
        tariffs.save(t3);

        Tariff t4 = new Tariff();
        t4.leviedAgainst = "Japan";
        t4.leviedBy = "USA";
        t4.leviedOn = "Cars";
        t4.rate = 0.025f;
        tariffs.save(t4);

        Tariff t5 = new Tariff();
        t5.leviedAgainst = "Canada";
        t5.leviedBy = "USA";
        t5.leviedOn = "Lumber";
        t5.rate = 0.1799f;
        tariffs.save(t5);

        Tariff t6 = new Tariff();
        t6.leviedAgainst = "Bangladesh";
        t6.leviedBy = "USA";
        t6.leviedOn = "Textiles";
        t6.rate = 0.158f;
        tariffs.save(t6);

        Tariff t7 = new Tariff();
        t7.leviedAgainst = "Mexico";
        t7.leviedBy = "USA";
        t7.leviedOn = "Trucks";
        t7.rate = 0.25f;
        tariffs.save(t7);

        Tariff t8 = new Tariff();
        t8.leviedAgainst = "Canada";
        t8.leviedBy = "USA";
        t8.leviedOn = "Copper";
        t8.rate = 0.0194f;
        tariffs.save(t8);

        assertIterableEquals(List.of("Copper", "Lumber"),
                             tariffs.findByLeviedAgainst("Canada").map(o -> o.leviedOn).sorted().collect(Collectors.toList()));

        // Iterator with paging:
        Iterator<Tariff> it = tariffs.findByLeviedAgainstLessThanOrderByKeyDesc("M");

        Tariff t;
        assertEquals(true, it.hasNext());
        assertNotNull(t = it.next());
        assertEquals(t8.leviedAgainst, t.leviedAgainst);

        assertEquals(true, it.hasNext());
        assertNotNull(t = it.next());
        assertEquals(t6.leviedAgainst, t.leviedAgainst);

        assertEquals(true, it.hasNext());
        assertNotNull(t = it.next());
        assertEquals(t5.leviedAgainst, t.leviedAgainst);

        assertEquals(true, it.hasNext());
        assertEquals(true, it.hasNext());
        assertNotNull(t = it.next());
        assertEquals(t4.leviedAgainst, t.leviedAgainst);

        assertNotNull(t = it.next());
        assertEquals(t3.leviedAgainst, t.leviedAgainst);

        assertNotNull(t = it.next());
        assertEquals(t2.leviedAgainst, t.leviedAgainst);

        assertNotNull(t = it.next());
        assertEquals(t1.leviedAgainst, t.leviedAgainst);

        assertEquals(false, it.hasNext());
        assertEquals(false, it.hasNext());

        // Paginated iterator with no results:
        it = tariffs.findByLeviedAgainstLessThanOrderByKeyDesc("A");
        assertEquals(false, it.hasNext());

        t = tariffs.findByLeviedByAndLeviedAgainstAndLeviedOn("USA", "Bangladesh", "Textiles");
        assertEquals(t6.rate, t.rate, 0.0001f);

        // List return type for Pagination only represents a single page, not all pages.
        // page 1:
        assertIterableEquals(List.of("China", "Germany", "India", "Japan"),
                             tariffs.findByLeviedByOrderByKey("USA", Pagination.page(1).size(4))
                                             .stream()
                                             .map(o -> o.leviedAgainst)
                                             .collect(Collectors.toList()));
        // page 2:
        assertIterableEquals(List.of("Canada", "Bangladesh", "Mexico", "Canada"),
                             tariffs.findByLeviedByOrderByKey("USA", Pagination.page(2).size(4))
                                             .stream()
                                             .map(o -> o.leviedAgainst)
                                             .collect(Collectors.toList()));

        // Random access to paginated list:
        List<Tariff> list = tariffs.findByLeviedByOrderByKey("USA", Pagination.page(1).size(20));
        assertEquals(t4.leviedAgainst, list.get(3).leviedAgainst);
        assertEquals(t7.leviedAgainst, list.get(6).leviedAgainst);
        assertEquals(t2.leviedAgainst, list.get(1).leviedAgainst);
        assertEquals(t8.leviedAgainst, list.get(7).leviedAgainst);

        assertEquals(8, tariffs.deleteByLeviedBy("USA"));
    }

    /**
     * Invoke methods that are annotated with the Select, Where, Update, and Delete annotations.
     */
    @Test
    public void testPartialQueryAnnotations() {
        Shipment s1 = new Shipment();
        s1.destination = "200 1st Ave SW, Rochester, MN 55902";
        s1.location = "44.027354, -92.468482";
        s1.id = 1;
        s1.orderedAt = OffsetDateTime.now().minusMinutes(45);
        s1.status = "IN_TRANSIT";
        shipments.save(s1);

        Shipment s2 = new Shipment();
        s2.destination = "201 4th St SE, Rochester, MN 55904";
        s2.location = "2800 37th St NW, Rochester, MN 55901";
        s2.id = 2;
        s2.orderedAt = OffsetDateTime.now().minusMinutes(20);
        s2.status = "READY_FOR_PICKUP";
        shipments.save(s2);

        Shipment s3 = new Shipment();
        s3.destination = "151 4th St SE, Rochester, MN 55904";
        s3.location = "44.057840, -92.496301";
        s3.id = 3;
        s3.orderedAt = OffsetDateTime.now().minusMinutes(13);
        s3.status = "IN_TRANSIT";
        shipments.save(s3);

        Shipment s4 = new Shipment();
        s4.destination = "151 4th St SE, Rochester, MN 55904";
        s4.location = "2800 37th St NW, Rochester, MN 55901 ";
        s4.id = 4;
        s4.orderedAt = OffsetDateTime.now().minusMinutes(4);
        s4.status = "READY_FOR_PICKUP";
        shipments.save(s4);

        Shipment s5 = new Shipment();
        s5.destination = "201 4th St SE, Rochester, MN 55904";
        s5.location = " 2800 37th St NW, Rochester, MN 55901";
        s5.id = 5;
        s5.orderedAt = OffsetDateTime.now().minusSeconds(50);
        s5.status = "PREPARING";
        shipments.save(s5);

        assertEquals(true, shipments.dispatch(2, "44.036217, -92.488040"));
        assertEquals("IN_TRANSIT", shipments.getStatus(2));

        // @OrderBy "destination"
        assertIterableEquals(List.of("151 4th St SE, Rochester, MN 55904",
                                     "200 1st Ave SW, Rochester, MN 55902",
                                     "201 4th St SE, Rochester, MN 55904"),
                             shipments.find("IN_TRANSIT")
                                             .map(o -> o.destination)
                                             .collect(Collectors.toList()));

        // @OrderBy "status", then "orderedAt" descending
        assertIterableEquals(List.of(3L, 2L, 1L, 5L, 4L),
                             Stream.of(shipments.getAll()).map(o -> o.id).collect(Collectors.toList()));

        Shipment s = shipments.find(3);
        String previousLocation = s.location;

        assertEquals(true, shipments.updateLocation(3, previousLocation, "44.029468, -92.483191"));
        assertEquals(false, shipments.updateLocation(3, previousLocation, "44.029406, -92.489553"));

        s = shipments.find(3);
        assertEquals("44.029468, -92.483191", s.location);

        assertEquals(true, shipments.cancel(4));
        assertEquals(true, shipments.cancel(5));
        assertEquals(false, shipments.cancel(10));

        shipments.trim();
        s = shipments.find(4);
        assertEquals("2800 37th St NW, Rochester, MN 55901", s.location);

        assertEquals(2, shipments.removeCanceled());

        assertEquals(3, shipments.removeEverything());
    }

    /**
     * Use the provided methods of a Repository<T, K> interface that is a copy of Jakarta NoSQL's.
     */
    @Test
    public void testRepositoryBuiltInMethods() {
        ZoneOffset CDT = ZoneOffset.ofHours(-5);

        // remove data that other tests previously inserted to the same table
        reservations.deleteByHostNot("never-ever-used@example.org");

        assertEquals(0, reservations.count());

        Reservation r1 = new Reservation();
        r1.host = "testRepository-host1@example.org";
        r1.invitees = Set.of("testRepository-1a@example.org", "testRepository-1b@example.org");
        r1.location = "050-2 G105";
        r1.meetingID = 10020001;
        r1.start = OffsetDateTime.of(2022, 5, 23, 9, 0, 0, 0, CDT);
        r1.stop = OffsetDateTime.of(2022, 5, 23, 10, 0, 0, 0, CDT);

        assertEquals(false, reservations.existsById(r1.meetingID));

        Reservation inserted = reservations.save(r1);
        assertEquals(r1.host, inserted.host);
        assertEquals(r1.invitees, inserted.invitees);
        assertEquals(r1.location, inserted.location);
        assertEquals(r1.meetingID, inserted.meetingID);
        assertEquals(r1.start, inserted.start);
        assertEquals(r1.stop, inserted.stop);

        assertEquals(true, reservations.existsById(r1.meetingID));

        assertEquals(1, reservations.count());

        Reservation r2 = new Reservation();
        r2.host = "testRepository-host2@example.org";
        r2.invitees = Set.of("testRepository-2a@example.org", "testRepository-2b@example.org");
        r2.location = "050-2 B120";
        r2.meetingID = 10020002;
        r2.start = OffsetDateTime.of(2022, 5, 23, 9, 0, 0, 0, CDT);
        r2.stop = OffsetDateTime.of(2022, 5, 23, 10, 0, 0, 0, CDT);

        Reservation r3 = new Reservation();
        r3.host = "testRepository-host2@example.org";
        r3.invitees = Set.of("testRepository-3a@example.org");
        r3.location = "030-2 A312";
        r3.meetingID = 10020003;
        r3.start = OffsetDateTime.of(2022, 5, 24, 8, 30, 0, 0, CDT);
        r3.stop = OffsetDateTime.of(2022, 5, 24, 10, 00, 0, 0, CDT);

        Reservation r4 = new Reservation();
        r4.host = "testRepository-host1@example.org";
        r4.invitees = Collections.emptySet();
        r4.location = "050-2 G105";
        r4.meetingID = 10020004;
        r4.start = OffsetDateTime.of(2022, 5, 24, 9, 0, 0, 0, CDT);
        r4.stop = OffsetDateTime.of(2022, 5, 24, 10, 0, 0, 0, CDT);

        r1.invitees = Set.of("testRepository-1a@example.org", "testRepository-1b@example.org", "testRepository-1c@example.org");

        Iterable<Reservation> insertedOrUpdated = reservations.save(new Iterable<>() {
            @Override
            public Iterator<Reservation> iterator() {
                return Arrays.asList(r1, r2, r3, r4).iterator();
            }
        });

        Iterator<Reservation> it = insertedOrUpdated.iterator();
        Reservation r;
        assertEquals(true, it.hasNext());
        assertNotNull(r = it.next());
        assertEquals(r1.meetingID, r.meetingID);
        assertEquals(r1.invitees, r.invitees);

        assertEquals(true, it.hasNext());
        assertNotNull(r = it.next());
        assertEquals(r2.meetingID, r.meetingID);

        assertEquals(true, it.hasNext());
        assertNotNull(r = it.next());
        assertEquals(r3.meetingID, r.meetingID);

        assertEquals(true, it.hasNext());
        assertNotNull(r = it.next());
        assertEquals(r4.meetingID, r.meetingID);

        assertEquals(false, it.hasNext());

        assertEquals(true, reservations.existsById(r3.meetingID));

        assertEquals(4, reservations.count());

        Reservation r2found = null, r4found = null;
        for (Reservation found : reservations.findById(List.of(r4.meetingID, r2.meetingID))) {
            if (found.meetingID == r2.meetingID && r2found == null)
                r2found = found;
            else if (found.meetingID == r4.meetingID && r4found == null)
                r4found = found;
            else
                fail("Found unexpected entity with meetingID of " + found.meetingID);
        }
        assertNotNull(r2found);
        assertNotNull(r4found);
        assertEquals(r2.location, r2found.location);
        assertEquals(r4.location, r4found.location);

        reservations.deleteById(r2.meetingID);

        Optional<Reservation> r2optional = reservations.findById(r2.meetingID);
        assertNotNull(r2optional);
        assertEquals(true, r2optional.isEmpty());

        assertEquals(3, reservations.count());

        reservations.deleteById(Set.of(r1.meetingID, r4.meetingID));

        assertEquals(false, reservations.existsById(r4.meetingID));

        assertEquals(1, reservations.count());

        Optional<Reservation> r3optional = reservations.findById(r3.meetingID);
        assertNotNull(r3optional);
        Reservation r3found = r3optional.get();
        assertEquals(r3.host, r3found.host);
        assertEquals(r3.invitees, r3found.invitees);
        assertEquals(r3.location, r3found.location);
        assertEquals(r3.meetingID, r3found.meetingID);
        assertEquals(r3.start.toInstant(), r3found.start.toInstant());
        assertEquals(r3.stop.toInstant(), r3found.stop.toInstant());
    }

    /**
     * Use custom repository interface methods modeled after Jakarta NoSQL's Repository class.
     *
     * @throws InterruptedException
     */
    @Test
    public void testRepositoryCustomMethods() throws InterruptedException {
        ZoneOffset CDT = ZoneOffset.ofHours(-5);

        // remove data that other tests previously inserted to the same table
        reservations.deleteByHostNotIn(Set.of("never-ever-used@example.org"));

        assertEquals(0, reservations.count());

        // set up some data for the test to use
        Reservation r1 = new Reservation();
        r1.host = "testRepositoryCustom-host1@example.org";
        r1.invitees = Set.of("testRepositoryCustom-1a@example.org", "testRepositoryCustom-1b@example.org", "testRepositoryCustom-1c@example.org");
        r1.location = "050-2 H115";
        r1.meetingID = 10030001;
        r1.start = OffsetDateTime.of(2022, 5, 25, 9, 0, 0, 0, CDT);
        r1.stop = OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT);

        Reservation r2 = new Reservation();
        r2.host = "testRepositoryCustom-host2@example.org";
        r2.invitees = Set.of("testRepositoryCustom-2a@example.org", "testRepositoryCustom-2b@example.org");
        r2.location = "050-2 A101";
        r2.meetingID = 10030002;
        r2.start = OffsetDateTime.of(2022, 5, 25, 9, 0, 0, 0, CDT);
        r2.stop = OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT);

        Reservation r3 = new Reservation();
        r3.host = "testRepositoryCustom-host3@example.org";
        r3.invitees = Set.of("testRepositoryCustom-3a@example.org", "testRepositoryCustom-3b@example.org");
        r3.location = "050-3 H103";
        r3.meetingID = 10030003;
        r3.start = OffsetDateTime.of(2022, 5, 25, 9, 0, 0, 0, CDT);
        r3.stop = OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT);

        Reservation r4 = new Reservation();
        r4.host = "testRepositoryCustom-host4@example.org";
        r4.invitees = Set.of("testRepositoryCustom-4a@example.org", "testRepositoryCustom-4b@example.org");
        r4.location = "050-2 H115";
        r4.meetingID = 10030004;
        r4.start = OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT);
        r4.stop = OffsetDateTime.of(2022, 5, 25, 11, 0, 0, 0, CDT);

        Reservation r5 = new Reservation();
        r5.host = "testRepositoryCustom-host2@example.org";
        r5.invitees = Set.of("testRepositoryCustom-5a@example.org", "testRepositoryCustom-5b@example.org");
        r5.location = "050-2 B120";
        r5.meetingID = 10030005;
        r5.start = OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT);
        r5.stop = OffsetDateTime.of(2022, 5, 25, 11, 0, 0, 0, CDT);

        Reservation r6 = new Reservation();
        r6.host = "testRepositoryCustom-host3@example.org";
        r6.invitees = Set.of("testRepositoryCustom-3c@example.org");
        r6.location = "050-2 G105";
        r6.meetingID = 10030006;
        r6.start = OffsetDateTime.of(2022, 5, 25, 11, 0, 0, 0, CDT);
        r6.stop = OffsetDateTime.of(2022, 5, 25, 12, 0, 0, 0, CDT);

        Reservation r7 = new Reservation();
        r7.host = "testRepositoryCustom-host2@example.org";
        r7.invitees = Set.of("testRepositoryCustom-2a@example.org", "testRepositoryCustom-2b@example.org");
        r7.location = "050-2 B120";
        r7.meetingID = 10030007;
        r7.start = OffsetDateTime.of(2022, 5, 25, 13, 0, 0, 0, CDT);
        r7.stop = OffsetDateTime.of(2022, 5, 25, 15, 0, 0, 0, CDT);

        Reservation r8 = new Reservation();
        r8.host = "testRepositoryCustom-host4@example.org";
        r8.invitees = Set.of("testRepositoryCustom-8a@example.org");
        r8.location = "030-2 E314";
        r8.meetingID = 10030008;
        r8.start = OffsetDateTime.of(2022, 5, 25, 13, 0, 0, 0, CDT);
        r8.stop = OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT);

        Reservation r9 = new Reservation();
        r9.host = "testRepositoryCustom-host3@example.org";
        r9.invitees = Collections.emptySet();
        r9.location = "050-2 B125";
        r9.meetingID = 10030009;
        r9.start = OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT);
        r9.stop = OffsetDateTime.of(2022, 5, 25, 15, 0, 0, 0, CDT);

        reservations.save(List.of(r1, r2, r3, r4, r5, r6, r7, r8, r9));

        List<Reservation> reservationList = new ArrayList<Reservation>();
        reservations.findByHost("testRepositoryCustom-host2@example.org").forEach(reservationList::add);
        assertIterableEquals(List.of(10030002L, 10030005L, 10030007L),
                             reservationList
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .sorted()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030005L, 10030007L, 10030009L),
                             reservations.findByLocationLikeOrderByMeetingID("-2 B1")
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030001L, 10030002L, 10030004L, 10030006L, 10030008L),
                             reservations.findByMeetingIDOrLocationLikeAndStartAndStopOrHost(10030006,
                                                                                             "050-2",
                                                                                             OffsetDateTime.of(2022, 5, 25, 9, 0, 0, 0, CDT),
                                                                                             OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT),
                                                                                             "testRepositoryCustom-host4@example.org")
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .sorted()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030004L, 10030005L),
                             reservations.findByStartBetweenAndLocationIn(OffsetDateTime.of(2022, 5, 25, 9, 30, 0, 0, CDT),
                                                                          OffsetDateTime.of(2022, 5, 25, 11, 30, 0, 0, CDT),
                                                                          List.of("050-2 H115", "050-2 B120", "050-2 B125"))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .sorted()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030009L, 10030007L, 10030008L, 10030006L),
                             reservations.findByStartGreaterThanOrderByStartDescOrderByStopDesc(OffsetDateTime.of(2022, 5, 25, 0, 0, 0, 0, CDT))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));

        Reservation[] array = reservations.findByStartLessThanOrStartGreaterThanOrderByMeetingIDDesc(OffsetDateTime.of(2022, 5, 25, 9, 30, 0, 0, CDT),
                                                                                                     OffsetDateTime.of(2022, 5, 25, 13, 30, 0, 0, CDT));
        assertArrayEquals(new Reservation[] { r9, r3, r2, r1 }, array,
                          Comparator.<Reservation, Long> comparing(o -> o.meetingID)
                                          .thenComparing(Comparator.<Reservation, String> comparing(o -> o.host))
                                          .thenComparing(Comparator.<Reservation, String> comparing(o -> o.invitees.toString()))
                                          .thenComparing(Comparator.<Reservation, String> comparing(o -> o.location))
                                          .thenComparing(Comparator.<Reservation, Instant> comparing(o -> o.start.toInstant()))
                                          .thenComparing(Comparator.<Reservation, Instant> comparing(o -> o.stop.toInstant())));

        assertIterableEquals(List.of(10030001L, 10030002L, 10030003L, 10030009L),
                             reservations.findByStartNotBetween(OffsetDateTime.of(2022, 5, 25, 9, 30, 0, 0, CDT),
                                                                OffsetDateTime.of(2022, 5, 25, 13, 30, 0, 0, CDT))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .sorted()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030007L, 10030008L, 10030009L),
                             reservations.findByStopGreaterThanEqual(OffsetDateTime.of(2022, 5, 25, 13, 0, 0, 0, CDT))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .sorted()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030003L, 10030001L, 10030004L, 10030006L, 10030009L, 10030005L, 10030007L, 10030002L, 10030008L),
                             reservations.findByStopGreaterThanOrderByLocationDescOrderByHostOrderByStopAsc(OffsetDateTime.of(2022, 5, 25, 8, 0, 0, 0, CDT))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030001L, 10030005L, 10030007L, 10030002L, 10030003L, 10030006L, 10030009L, 10030004L, 10030008L),
                             reservations.findByStopLessThanOrderByHostAscOrderByLocationDescOrderByStart(OffsetDateTime.of(2022, 5, 26, 0, 0, 0, 0, CDT))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));

        // Reverse ordering to the above using Sorts:
        assertIterableEquals(List.of(10030008L, 10030004L, 10030009L, 10030006L, 10030003L, 10030002L, 10030007L, 10030005L, 10030001L),
                             reservations.findByStopLessThan(OffsetDateTime.of(2022, 5, 26, 0, 0, 0, 0, CDT),
                                                             Sorts.sorts()
                                                                             .add(Sort.desc("host"))
                                                                             .add(Sort.asc("location"))
                                                                             .add(Sort.desc("start")))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030001L, 10030002L, 10030003L, 10030007L, 10030008L),
                             reservations.findByStopOrStart(OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT),
                                                            OffsetDateTime.of(2022, 5, 25, 13, 0, 0, 0, CDT))
                                             .map(r -> r.meetingID)
                                             .sorted()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of("030-2 E314", "050-2 B125", "050-2 G105"),
                             reservations.findByStopOrStartOrStart(OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT),
                                                                   OffsetDateTime.of(2022, 5, 25, 11, 0, 0, 0, CDT),
                                                                   OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT))
                                             .parallel()
                                             .sorted()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(10030004L, 10030005L, 10030006L, 10030009L),
                             reservations.findByStopOrStartOrStartOrStart(OffsetDateTime.of(2022, 5, 25, 11, 0, 0, 0, CDT),
                                                                          OffsetDateTime.of(2022, 5, 25, 7, 30, 0, 0, CDT),
                                                                          OffsetDateTime.of(2022, 5, 25, 11, 0, 0, 0, CDT),
                                                                          OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT))
                                             .parallel()
                                             .sorted()
                                             .boxed()
                                             .collect(Collectors.toList()));

        assertIterableEquals(List.of(OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT).toInstant(),
                                     OffsetDateTime.of(2022, 5, 25, 10, 0, 0, 0, CDT).toInstant(),
                                     OffsetDateTime.of(2022, 5, 25, 13, 0, 0, 0, CDT).toInstant(),
                                     OffsetDateTime.of(2022, 5, 25, 13, 0, 0, 0, CDT).toInstant(),
                                     OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT).toInstant()),
                             reservations.findByStopOrStopOrStop(OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT),
                                                                 OffsetDateTime.of(2022, 5, 25, 15, 0, 0, 0, CDT),
                                                                 OffsetDateTime.of(2022, 5, 25, 11, 0, 0, 0, CDT))
                                             .map(r -> r.start().toInstant())
                                             .sorted()
                                             .collect(Collectors.toList()));

        Publisher<Reservation> publisher = reservations.findByHostLikeOrderByMeetingID("testRepositoryCustom-host");
        LinkedBlockingQueue<Object> results = new LinkedBlockingQueue<>();
        publisher.subscribe(new Subscriber<Reservation>() {
            final int REQUEST_SIZE = 3;
            int count = 0;
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(REQUEST_SIZE);
            }

            @Override
            public void onNext(Reservation item) {
                results.add(item);
                if (++count % REQUEST_SIZE == 0)
                    subscription.request(REQUEST_SIZE);
            }

            @Override
            public void onError(Throwable x) {
                results.add(x);
            }

            @Override
            public void onComplete() {
                results.add("DONE");
            }
        });

        Object result;
        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030001L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030002L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030003L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030004L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030005L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030006L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030007L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030008L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals(10030009L, ((Reservation) result).meetingID);

        assertNotNull(result = results.poll(TIMEOUT_MINUTES, TimeUnit.MINUTES));
        if (result instanceof Throwable)
            throw new AssertionError("onError notification received", (Throwable) result);
        assertEquals("DONE", result);

        // Paging where the final page includes less than the maximum page size,
        Page<Reservation> page1 = reservations.findByHostLike("testRepositoryCustom-host",
                                                              Pagination.page(1).size(4),
                                                              Sort.desc("meetingID"));
        assertIterableEquals(List.of(10030009L, 10030008L, 10030007L, 10030006L),
                             page1
                                             .getContent()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));
        Page<Reservation> page2 = page1.next();
        assertIterableEquals(List.of(10030005L, 10030004L, 10030003L, 10030002L),
                             page2
                                             .get()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));
        Page<Reservation> page3 = page2.next();
        assertIterableEquals(List.of(10030001L),
                             page3
                                             .getContent()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));
        assertEquals(null, page3.next());

        // Paging that comes out even:
        page2 = reservations.findByHostLike("testRepositoryCustom-host",
                                            Pagination.page(2).size(3),
                                            Sort.desc("meetingID"));
        assertIterableEquals(List.of(10030006L, 10030005L, 10030004L),
                             page2
                                             .getContent()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));
        page3 = page2.next();
        assertIterableEquals(List.of(10030003L, 10030002L, 10030001L),
                             page3
                                             .getContent()
                                             .map(r -> r.meetingID)
                                             .collect(Collectors.toList()));
        assertEquals(null, page3.next());

        assertEquals(false, reservations.deleteByHostIn(List.of("testRepositoryCustom-host5@example.org")));

        assertEquals(true, reservations.deleteByHostIn(List.of("testRepositoryCustom-host1@example.org",
                                                               "testRepositoryCustom-host3@example.org")));

        assertIterableEquals(List.of(10030002L, 10030004L, 10030005L, 10030008L),
                             reservations.findByStopLessThanEqual(OffsetDateTime.of(2022, 5, 25, 14, 0, 0, 0, CDT))
                                             .stream()
                                             .map(r -> r.meetingID)
                                             .sorted()
                                             .collect(Collectors.toList()));
    }

    /**
     * Experiment with making a repository method return a record-like type.
     */
    @Test
    public void testSelectAsRecord() {
        ZoneOffset CDT = ZoneOffset.ofHours(-5);

        // remove data that other tests previously inserted to the same table
        reservations.deleteByHostNotIn(Set.of("never-ever-used@example.org"));

        assertEquals(0, reservations.count());

        // set up some data for the test to use
        Reservation r1 = new Reservation();
        r1.host = "testSelectAsRecord-host1@example.org";
        r1.invitees = Set.of("testSelectAsRecord-1a@example.org", "testSelectAsRecord-1b@example.org");
        r1.location = "30-2 C206";
        r1.meetingID = 10040001;
        r1.start = OffsetDateTime.of(2022, 6, 3, 13, 30, 0, 0, CDT);
        r1.stop = OffsetDateTime.of(2022, 6, 3, 15, 0, 0, 0, CDT);

        Reservation r2 = new Reservation();
        r2.host = "testSelectAsRecord-host2@example.org";
        r2.invitees = Set.of("testSelectAsRecord-2a@example.org", "testSelectAsRecord-2b@example.org");
        r2.location = "30-2 C206";
        r2.meetingID = 10040002;
        r2.start = OffsetDateTime.of(2022, 6, 3, 9, 0, 0, 0, CDT);
        r2.stop = OffsetDateTime.of(2022, 6, 3, 10, 0, 0, 0, CDT);

        Reservation r3 = new Reservation();
        r3.host = "testSelectAsRecord-host3@example.org";
        r3.invitees = Set.of("testSelectAsRecord-3a@example.org");
        r3.location = "30-2 C206";
        r3.meetingID = 10040003;
        r3.start = OffsetDateTime.of(2022, 6, 3, 15, 0, 0, 0, CDT);
        r3.stop = OffsetDateTime.of(2022, 6, 3, 16, 0, 0, 0, CDT);

        Reservation r4 = new Reservation();
        r4.host = "testSelectAsRecord-host3@example.org";
        r4.invitees = Set.of("testSelectAsRecord-3a@example.org", "testSelectAsRecord-3b@example.org");
        r4.location = "30-2 C220";
        r4.meetingID = 10040004;
        r4.start = OffsetDateTime.of(2022, 6, 3, 9, 0, 0, 0, CDT);
        r4.stop = OffsetDateTime.of(2022, 6, 3, 10, 0, 0, 0, CDT);

        reservations.save(Set.of(r1, r2, r3, r4));

        ReservedTimeSlot[] reserved = reservations.findByLocationAndStartBetweenOrderByStart("30-2 C206",
                                                                                             OffsetDateTime.of(2022, 6, 3, 0, 0, 0, 0, CDT),
                                                                                             OffsetDateTime.of(2022, 6, 3, 23, 59, 59, 0, CDT));
        assertArrayEquals(new ReservedTimeSlot[] { new ReservedTimeSlot(r2.start, r2.stop),
                                                   new ReservedTimeSlot(r1.start, r1.stop),
                                                   new ReservedTimeSlot(r3.start, r3.stop) },
                          reserved,
                          Comparator.<ReservedTimeSlot, Instant> comparing(o -> o.start().toInstant())
                                          .thenComparing(Comparator.<ReservedTimeSlot, Instant> comparing(o -> o.stop().toInstant())));
    }

    /**
     * Update multiple entries.
     */
    @Test
    public void testUpdateMultiple() {
        assertEquals(0, products.putOnSale("TestUpdateMultiple-match", .10f));

        Product prod1 = new Product();
        prod1.id = "800-2024-S";
        prod1.name = "Small size TestUpdateMultiple-matched item";
        prod1.price = 10.00f;
        products.insert(prod1);

        Product prod2 = new Product();
        prod2.id = "800-3024-M";
        prod2.name = "Medium size TestUpdateMultiple-matched item";
        prod2.price = 15.00f;
        products.insert(prod2);

        Product prod3 = new Product();
        prod3.id = "C6000-814BH0003Y";
        prod3.name = "Medium size TestUpdateMultiple non-matching item";
        prod3.price = 18.00f;
        products.insert(prod3);

        Product prod4 = new Product();
        prod4.id = "800-4024-L";
        prod4.name = "Large size TestUpdateMultiple-matched item";
        prod4.price = 20.00f;
        products.insert(prod4);

        assertEquals(3, products.putOnSale("TestUpdateMultiple-match", .20f));

        Product p1 = products.findItem(prod1.id);
        assertEquals(8.00f, p1.price, 0.001f);

        Product p2 = products.findItem(prod2.id);
        assertEquals(12.00f, p2.price, 0.001f);

        Product p3 = products.findItem(prod3.id);
        assertEquals(prod3.price, p3.price, 0.001f);

        Product p4 = products.findItem(prod4.id);
        assertEquals(16.00f, p4.price, 0.001f);
    }
}
