# Task 22: Cloud Service Providers Comparative Study (AWS, GCP, Azure)

## Overview
A comprehensive technical and financial comparison of the top three hyperscale cloud service providers: **Amazon Web Services (AWS)**, **Google Cloud Platform (GCP)**, and **Microsoft Azure**. Evaluates foundational cloud pillars: **Compute**, **Storage**, **Databases**, **Networking**, **Security**, and **Pay-as-you-use Pricing Models**, including an architectural blueprint for deploying our Full Stack Microservices portfolio.

---

## Service Mapping At A Glance

```
+------------------------------------+--------------------------+--------------------------+--------------------------+
| Architectural Domain               | AWS                      | Google Cloud (GCP)       | Microsoft Azure          |
+------------------------------------+--------------------------+--------------------------+--------------------------+
| Elastic Virtual Machines           | EC2                      | Compute Engine (GCE)     | Azure Virtual Machines   |
| Container Orchestration (K8s)     | EKS                      | GKE (Autopilot)          | AKS                      |
| Serverless Microservices           | Lambda / Fargate         | Cloud Run                | Azure Container Apps     |
| Highly Available Object Storage    | S3                       | Cloud Storage (GCS)      | Azure Blob Storage       |
| Relational OLTP Database           | RDS / Aurora             | Cloud SQL / AlloyDB      | Azure SQL / Flexible Svr |
| Global Non-Relational / NoSQL      | DynamoDB                 | Cloud Spanner / Firestore| Cosmos DB                |
| Virtual Private Networking         | VPC (Regional)           | Cloud VPC (Global)       | VNet (Regional)          |
| L7 Ingress & Load Balancing        | ALB                      | Cloud Load Balancing     | Application Gateway      |
+------------------------------------+--------------------------+--------------------------+--------------------------+
```

---

## Artifacts in this Directory

- `CLOUD_COMPARISON_REPORT.md`: Comprehensive academic and industry comparison document detailing technical architecture, SLA guarantees, data tiering, global networking differences, and pricing models.
- `cloud_cost_calculator.py`: Interactive command-line simulation utility that calculates and compares monthly infrastructure expenses across all three providers for customizable production workload profiles.
- `Run.txt`: Execution commands for running the cost estimator and reading the report.

---

## Execution: Running the Cloud Cost Estimator

Run the calculator with Python:
```bash
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 22 - Cloud Service Providers Comparative Study"
python cloud_cost_calculator.py
```

### Sample Output
```text
=================================================================
 TASK 22: HYPERSCALE CLOUD COST ESTIMATION MATRIX
=================================================================
Workload Profile:
  - Compute Cores:   4 vCPUs (730 hrs/month)
  - System Memory:   16 GB RAM
  - Database Store:  100 GB SSD (Managed MySQL/Postgres)
  - Egress Traffic:  250 GB/month outbound
-----------------------------------------------------------------
Provider   | Compute      | Database     | Network    | Total / Month 
-----------------------------------------------------------------
AWS        | $    174.73  | $     66.50  | $   13.50  | $      254.73
GCP        | $    141.79  | $     65.00  | $   12.75  | $      219.54
Azure      | $    175.20  | $     64.50  | $   13.05  | $      252.75
=================================================================
Most Cost-Effective Provider for this workload: GCP ($219.54/mo)
=================================================================
```
