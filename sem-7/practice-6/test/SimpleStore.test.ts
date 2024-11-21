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
});
