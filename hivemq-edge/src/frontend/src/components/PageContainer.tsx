import { Heading, Text, VisuallyHidden } from "@chakra-ui/react"
import { ReactNode } from "react"
import { useTranslation } from "react-i18next"

import { useRouterNavigation } from "@/hooks/useRouterNavigation"
import useGetNavItems from "@/modules/Dashboard/hooks/useGetNavItems"
import { HmqUIShell, useOverlayState } from "@hivemq/ui-component-library"
import {
  BellIcon,
  CircleUserIcon,
  GripIcon,
} from "lucide-react"
import { Link } from "react-router-dom"
import hivemqBeeNeg from "../assets/hivemq-bee-edge-neg.svg"
// import { UserBadge } from '../auth/UserBadge'
// import { HmqUIShellPanelAppSwitcher } from './ToolbarPanelAppSwitcher'

const pageNeedsFullscreen = false

// const linksList = ["Support", "Docs"]

type PageContainerProps = {
  title?: string
  subtitle?: string
  children?: ReactNode
  cta?: ReactNode
}
function PageContainer({ children, title, subtitle, cta }: PageContainerProps) {
  const { t } = useTranslation()

  const { data: items } = useGetNavItems()

  const navigation = [
    {
      name: "Edge",
      root: "/",
      href: "/",
      sideNav: items.map(({ title, items }) => ({
        sectionName: title,
        items: items.map((item) => ({
          href: item.href || "",
          name: item.label || "",
        })),
      })),
    },
  ]

  const isRouterNavigationItemActive = useRouterNavigation()

  const { setCurrentOverlayId, isOpenSideNav, anyOverlayOpen } =
    useOverlayState()

  return (
    <HmqUIShell.Grid>
      <HmqUIShell.TopLevelNavigation>
        <HmqUIShell.SideNavMenuToolbarButton />

        <HmqUIShell.Logo logo={hivemqBeeNeg}>Edge</HmqUIShell.Logo>

        <div className="flex items-center">
          <HmqUIShell.HeaderStatus title="Status" indicator="success">
            Running
          </HmqUIShell.HeaderStatus>
          <HmqUIShell.HeaderStatus title="RPM">42.8k</HmqUIShell.HeaderStatus>
        </div>

        <HmqUIShell.Toolbar className="ms-auto">
          <HmqUIShell.ToolbarButtonIcon>
            <BellIcon />
          </HmqUIShell.ToolbarButtonIcon>
          <HmqUIShell.ToolbarButtonIcon>
            <CircleUserIcon />
          </HmqUIShell.ToolbarButtonIcon>
          <HmqUIShell.ToolbarButtonIcon>
            <GripIcon />
          </HmqUIShell.ToolbarButtonIcon>
          {/* <UserBadge
            onLogout={() => {
              setCurrentOverlayId()
            }}
          />
          <HmqUIShellPanelAppSwitcher linksList={linksList} /> */}
        </HmqUIShell.Toolbar>
      </HmqUIShell.TopLevelNavigation>

      <HmqUIShell.PageLevelNavigation
        isOpenMenu={isOpenSideNav}
        pageNeedsFullscreen={pageNeedsFullscreen}
      >
        <HmqUIShell.SidebarNavigation
          navigation={navigation}
          renderNavLink={renderNavLink}
          isRootActive={() => true}
          isHrefActive={isRouterNavigationItemActive}
        />
      </HmqUIShell.PageLevelNavigation>

      <HmqUIShell.Content>
        {anyOverlayOpen && (
          <HmqUIShell.Overlay onClick={() => setCurrentOverlayId()} />
        )}

        <div className="mx-12 my-6">
          <Heading as={"h1"} className="!font-display">
            {title ? (
              title
            ) : (
              <VisuallyHidden>
                <h1>{t("translation:navigation.mainPage")}</h1>
              </VisuallyHidden>
            )}
            {cta}
          </Heading>
          {subtitle && <Text fontSize="md">{subtitle}</Text>}

          {children}
        </div>
      </HmqUIShell.Content>
    </HmqUIShell.Grid>
  )
}

export default PageContainer

const renderNavLink = (
  sideNavItem: { href: string; name: string },
  className: string,
) => {
  return (
    <Link to={sideNavItem.href} className={className}>
      {sideNavItem.name}
    </Link>
  )
}
