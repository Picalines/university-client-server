import { loadFixture } from "@nomicfoundation/hardhat-toolbox/network-helpers";
import { expect } from "chai";
import hre from "hardhat";

describe("SimpleStore", () => {
  const deployOneYearLockFixture = async () => {
    const ONE_GWEI = 1_000_000_000;

    const lockedAmount = ONE_GWEI;

    const [owner, customer] = await hre.ethers.getSigners();

    const SimpleStore = await hre.ethers.getContractFactory("SimpleStore");
    const store = await SimpleStore.deploy();

    return { store, lockedAmount, owner, customer };
  };

  describe("Deployment", () => {
    it("Should set the right owner", async () => {
      const { store, owner } = await loadFixture(deployOneYearLockFixture);

      expect(await store.owner()).to.equal(owner.address);
    });
  });

  describe("Events", () => {
    it("should emit ProductAdded event after addProduct called", async () => {
      const { store } = await loadFixture(deployOneYearLockFixture);

      await expect(store.addProduct("product", 123, 5))
        .to.emit(store, "ProductAdded")
        .withArgs([1n, "product", 123n, 5n]);
    });

    it("should emit ProductPurchased event after purchaseProduct called", async () => {
      const { store } = await loadFixture(deployOneYearLockFixture);

      await store.addProduct("product", 100n, 100n);

      await expect(store.purchaseProduct(1n, 1n, { value: 100 }))
        .to.emit(store, "ProductPurchased")
        .withArgs([1n, "product", 100n, 1n]);
    });
  });

  describe("Payment", () => {
    it("should send eth to the owner on purchaseProduct", async () => {
      const { store, owner, customer } = await loadFixture(
        deployOneYearLockFixture
      );

      await store.addProduct("product", 100n, 1n);

      await expect(
        store.connect(customer).purchaseProduct(1n, 1n, { value: 100 })
      ).to.changeEtherBalances([owner, customer], [100n, -100n]);
    });

    it("should not send more eth than the product costs", async () => {
      const { store, owner, customer } = await loadFixture(
        deployOneYearLockFixture
      );

      await store.addProduct("product", 100n, 1n);

      await expect(
        store.connect(customer).purchaseProduct(1n, 1n, { value: 900 })
      ).to.changeEtherBalances([owner, customer], [100n, -100n]);
    });

    it("should revert if there's not enough eth", async () => {
      const { store, customer } = await loadFixture(deployOneYearLockFixture);

      await store.addProduct("product", 100n, 1n);

      await expect(
        store.connect(customer).purchaseProduct(1n, 1n, { value: 50 })
      ).to.revertedWith("Insufficient funds sent");
    });

    it("should revert if there's not enough products", async () => {
      const { store, customer } = await loadFixture(deployOneYearLockFixture);

      await store.addProduct("product", 100n, 1n);

      await expect(
        store.connect(customer).purchaseProduct(1n, 3n, { value: 100 })
      ).to.revertedWith("Not enough stock available");
    });
  });
});
