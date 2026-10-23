# Enterprise Cloud Comparative Study: AWS vs. Google Cloud vs. Microsoft Azure

## Executive Summary
This document delivers a technical and economic comparative analysis of the three leading hyperscale cloud service providers: **Amazon Web Services (AWS)**, **Google Cloud Platform (GCP)**, and **Microsoft Azure**. The evaluation centers on six architectural pillars: **Compute**, **Storage**, **Databases**, **Networking**, **Security & Identity**, and **Pricing Models (Pay-as-you-use & Cost Optimization)**, culminating in an architectural blueprint for hosting the Full Stack Microservices Portfolio.

---

## 1. Core Services Comparison Matrix

| Architectural Pillar | Amazon Web Services (AWS) | Google Cloud Platform (GCP) | Microsoft Azure |
|---|---|---|---|
| **IaaS Virtual Machines** | Amazon EC2 | Google Compute Engine (GCE) | Azure Virtual Machines |
| **Managed Kubernetes** | Amazon EKS | Google Kubernetes Engine (GKE) | Azure Kubernetes Service (AKS) |
| **Serverless Containers** | AWS Fargate / App Runner | Cloud Run | Azure Container Apps |
| **Function-as-a-Service (FaaS)** | AWS Lambda | Cloud Functions / Cloud Run functions | Azure Functions |
| **Object Storage** | Amazon S3 | Google Cloud Storage (GCS) | Azure Blob Storage |
| **Block Storage** | Amazon EBS | Google Persistent Disk (PD) / Hyperdisk | Azure Managed Disks |
| **Managed Relational DB** | Amazon RDS / Amazon Aurora | Cloud SQL / AlloyDB for PostgreSQL | Azure Database for MySQL / Azure SQL |
| **Globally Distributed DB** | Amazon DynamoDB / Aurora Multi-Master | Google Cloud Spanner | Azure Cosmos DB |
| **In-Memory Caching** | Amazon ElastiCache (Redis/Memcached) | Google Cloud Memorystore | Azure Cache for Redis |
| **Virtual Networking** | Amazon VPC (Regional) | Google Cloud VPC (Global by default) | Azure Virtual Network (VNet) |
| **Layer 7 Load Balancing** | Application Load Balancer (ALB) | Cloud Load Balancing (Anycast IP) | Azure Application Gateway |
| **Content Delivery Network** | Amazon CloudFront | Google Cloud CDN | Azure Front Door / CDN |

---

## 2. In-Depth Pillar Analysis

### A. Compute
- **AWS**: Offers the broadest hardware diversity with custom Graviton (ARM) processors, inferentia AI chips, and extensive bare-metal instances.
- **GCP**: Renowned for container leadership with GKE (Autopilot mode eliminates node management), fast VM boot times (< 30 seconds), and custom machine types allowing arbitrary vCPU-to-memory ratios.
- **Azure**: Seamless enterprise hybrid integration with Windows Server, Active Directory, Azure Arc, and deep enterprise agreement discounts.

### B. Storage
- **Object Storage**:
  - AWS S3 provides 11 9s (99.999999999%) durability with intelligent tiering.
  - GCP Cloud Storage offers uniform APIs across regional, dual-region, and multi-region buckets with sub-second retrieval times on coldline/archive tiers.
  - Azure Blob Storage offers Hot, Cool, Cold, and Archive access tiers with hierarchical namespace support for Azure Data Lake Storage Gen2.
- **Block Storage**: GCP Hyperdisk and AWS EBS io2 Block Express offer up to 256,000 IOPS for high-throughput database workloads.

### C. Databases
- **Relational**:
  - AWS Aurora provides high-throughput MySQL/PostgreSQL compatibility with 6-way storage replication across 3 AZs.
  - GCP Cloud SQL offers managed MySQL/PostgreSQL, while AlloyDB provides up to 4x faster transactional performance for PostgreSQL workloads.
  - Azure SQL Database provides hyperscale auto-scaling storage up to 100 TB.
- **NewSQL / Distributed**: Google Cloud Spanner offers external consistency (TrueTime atomic clocks) with zero-downtime schema migrations. Azure Cosmos DB provides multi-model APIs with guaranteed single-digit millisecond latency.

### D. Networking
- **GCP Advantage**: Operates a private, software-defined global fiber network. VPCs are global resources; subnets are regional. Anycast single-IP global load balancing automatically routes traffic to the closest healthy region.
- **AWS**: Region-bound VPCs connected via VPC Peering, Transit Gateways, or AWS Direct Connect.
- **Azure**: Regional VNets connected via VNet Peering, ExpressRoute, and Azure Virtual WAN.

---

## 3. Pay-As-You-Use Pricing & Cost Optimization Models

### A. Billing Granularity
- **GCP**: Per-second billing with a 1-minute minimum. Automatic **Sustained Use Discounts (SUD)** apply up to 30% savings for instances running continuously without upfront commitments.
- **AWS**: Per-second billing for Linux instances; Savings Plans (Compute / EC2) and Reserved Instances (RI) offer up to 72% discounts for 1- or 3-year commitments.
- **Azure**: Per-second billing; Azure Reserved VM Instances provide up to 72% discounts, expandable to 80% with Azure Hybrid Benefit for existing Windows/SQL licenses.

### B. Spot / Preemptible Instances
- Providers auction spare compute capacity with steep discounts (60% to 90% savings):
  - AWS Spot Instances (with 2-minute termination warnings).
  - GCP Spot VMs (dynamic pricing with 30-second preemption notices).
  - Azure Spot VMs (eviction based on capacity or max price constraints).

---

## 4. Architecture Hosting Blueprint for Full Stack Microservices

To host our **Full Stack Microservices Portfolio** (API Gateway, Eureka, Product Service, Order Service, and MySQL):

```
                                  [ Internet Traffic ]
                                           |
                                           v
                       +---------------------------------------+
                       | Global Anycast CDN & Load Balancer    |
                       | (AWS CloudFront / GCP Cloud CDN)      |
                       +---------------------------------------+
                                           |
                                           v
                       +---------------------------------------+
                       |       Managed Kubernetes Cluster      |
                       |       (AWS EKS / GCP GKE Autopilot)   |
                       |  - Namespace: fullstack-production    |
                       |  - Pod: API Gateway (:8080)           |
                       |  - Pod: Eureka Registry (:8761)       |
                       |  - Pod: Product Service (:8081)       |
                       |  - Pod: Order Service (:8082)         |
                       +---------------------------------------+
                                           |
                                           v (Private Subnet VPC)
                       +---------------------------------------+
                       |      Managed Relational Database      |
                       |      (AWS RDS MySQL / GCP Cloud SQL)  |
                       |      - Multi-AZ High Availability     |
                       |      - Automated Daily Snapshots      |
                       +---------------------------------------+
```

### Estimated Monthly Spend Breakdown (Production Tier)
- Compute (2 Nodes / 4 vCPUs / 16GB RAM): ~$120/mo
- Managed Relational Database (db.t4g.medium / 50GB SSD): ~$65/mo
- Load Balancer & Egress Bandwidth (~100 GB): ~$25/mo
- **Estimated Total**: **~$210 / month** across any major provider.
